package com.ziliang.seckill.controller;

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

@Controller
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


    private HashMap<Long, Boolean> localOverMap =  new HashMap<Long, Boolean>();

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

    // 管理后台来做重置商品这种操作
    @RequestMapping(value="/reset", method=RequestMethod.GET)
    @ResponseBody
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

    /**
     * QPS:1306
     * 5000 * 10
     * QPS: 2114
     * */
    @RequestMapping(value="/{path}/do_seckill", method=RequestMethod.POST)
    @ResponseBody
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
        }{
            return Result.error(CodeMsg.SESSION_ERROR);
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
    @RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> seckillResult(Model model,SeckillUser user,
                                      @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result  =seckillService.getSeckillResult(user.getId(), goodsId);
        return Result.success(result);
    }

    @RequestMapping(value="/path", method=RequestMethod.GET)
    @ResponseBody
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


    @RequestMapping(value="/verifyCode", method=RequestMethod.GET)
    @ResponseBody
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
    /*
     *
     * QPS:1306
     * 5000 * 10
     * 目前在高并发下,出现超卖

    @RequestMapping("/do_seckill")
    public String list(Model model, SeckillUser user,
                       @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return "login";
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.SECKILL_OVER.getMsg());
            return "seckill_fail";
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEATE_SECKILL.getMsg());
            return "seckill_fail";
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }
    */

    /*
    // 秒杀接口,前后端分离
    @RequestMapping(value="/do_seckill", method= RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> seckill(Model model,SeckillUser user,
                                     @RequestParam("goodsId")long goodsId) {
        model.addAttribute("user", user);
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        //判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);//10个商品，req1 req2
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return Result.error(CodeMsg.SECKILL_OVER);
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return Result.error(CodeMsg.REPEATE_SECKILL);
        }
        //减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = seckillService.seckill(user, goods);
        return Result.success(orderInfo);
    }
    */
}
