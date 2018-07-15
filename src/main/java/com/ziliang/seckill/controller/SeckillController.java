package com.ziliang.seckill.controller;

import com.ziliang.seckill.access.AccessLimit;
import com.ziliang.seckill.domain.OrderInfo;
import com.ziliang.seckill.domain.SeckillOrder;
import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.rabbitmq.MQSender;
import com.ziliang.seckill.rabbitmq.SeckillMessage;
import com.ziliang.seckill.redis.GoodsKey;
import com.ziliang.seckill.redis.OrderKey;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.redis.SeckillKey;
import com.ziliang.seckill.result.CodeMsg;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.GoodsService;
import com.ziliang.seckill.service.OrderService;
import com.ziliang.seckill.service.SeckillService;
import com.ziliang.seckill.service.SeckillUserService;
import com.ziliang.seckill.vo.GoodsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Api(value = "秒杀controller", tags = {"秒杀操作接口"})
@RestController
@CrossOrigin(origins = {"http://localhost:8081"}, allowCredentials = "true")
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender sender;


    private HashMap<Long, Boolean> localOverMap =  new HashMap<>();

    /**
     * InitializingBean
     * 系统初始化
     * */
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if(goodsList == null) {
            return;
        }
        for(GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getSeckillGoodsStock, ""+goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    @ApiOperation(value = "重置操作", notes = "为了方便我们测试，把数据库和缓存进行重置")
    @GetMapping(value="/reset")
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for(GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getSeckillGoodsStock, ""+goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getSeckillOrderByUidGid);
        redisService.delete(SeckillKey.isGoodsOver);
        seckillService.reset(goodsList);
        return Result.success(true);
    }

    @ApiOperation(value = "执行秒杀操作")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "long", paramType = "query"),
            @ApiImplicitParam(name = "path", value = "秒杀地址", required = true, dataType = "String", paramType = "path")
    })
    @PostMapping(value="/{path}/do_seckill")
    public Result<Integer> seckill(Model model,SeckillUser user,
                                   @RequestParam("goodsId")long goodsId,
                                   @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //验证path
        boolean check = seckillService.checkPath(user, goodsId, path);
        if(!check){
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        //内存标记，减少redis访问
        boolean over = localOverMap.get(goodsId);
        if(over) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //预减库存
        long stock = redisService.decr(GoodsKey.getSeckillGoodsStock, ""+goodsId);//10
        if(stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //入队
        SeckillMessage mm = new SeckillMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendSeckillMessage(mm);
        return Result.success(0);//排队中

    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     * */
    @ApiOperation(value = "获取秒杀结果", notes = "秒杀成功返回订单id，秒杀失败返回-1,排队中返回0")
    @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "long")
    @GetMapping(value="/result")
    public Result<Long> seckillResult(Model model,SeckillUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @ApiOperation(value = "获取秒杀地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "long"),
            @ApiImplicitParam(name = "verifyCode", value = "验证码", required = true, dataType = "int", defaultValue = "0")
    })
    @AccessLimit(seconds=5, maxCount=5, needLogin=true)
    @GetMapping(value="/path")
    public Result<String> getSeckillPath(HttpServletRequest request, SeckillUser user,
                                         @RequestParam("goodsId")long goodsId,
                                         @RequestParam(value="verifyCode", defaultValue="0")int verifyCode
    ) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        boolean check = seckillService.checkVerifyCode(user, goodsId, verifyCode);
        if(!check) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path  =seckillService.createSeckillPath(user, goodsId);
        return Result.success(path);
    }

    @ApiOperation(value = "获取验证码")
    @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "long")
    @GetMapping(value="/verifyCode")
    public Result<String> getSeckillaVerifyCode(HttpServletResponse response, SeckillUser user,
                                               @RequestParam("goodsId")long goodsId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image  = seckillService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        }catch(Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.SECKILL_FAIL);
        }
    }

}
