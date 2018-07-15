package com.ziliang.seckill.controller;

import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.GoodsKey;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.GoodsService;
import com.ziliang.seckill.service.SeckillUserService;
import com.ziliang.seckill.vo.GoodsDetailVo;
import com.ziliang.seckill.vo.GoodsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "商品展示controller", tags = {"获取商品信息"})
@RestController
@CrossOrigin(origins = {"http://localhost:8081"}, allowCredentials = "true")
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @ApiOperation(value = "获取商品列表")
    @GetMapping(value="/to_list")
    public Result<List<GoodsVo>> list(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        return Result.success(goodsList);

    }

    @ApiOperation(value = "获取商品详情", notes = "由商品id获取商品详情")
    @ApiImplicitParam(name = "goodsId", value = "商品id", required = true, dataType = "long")
    @GetMapping(value="/detail/{goodsId}")
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, SeckillUser user,
                                        @PathVariable("goodsId")long goodsId) {
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if(now < startAt ) {//秒杀还没开始，倒计时
            seckillStatus = 0;
            remainSeconds = (int)((startAt - now )/1000);
        }else  if(now > endAt){//秒杀已经结束
            seckillStatus = 2;
            remainSeconds = -1;
        }else {//秒杀进行中
            seckillStatus = 1;
            remainSeconds = 0;
        }

        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);
        return Result.success(vo);
    }

}
