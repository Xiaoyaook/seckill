package com.ziliang.seckill.controller;

import com.ziliang.seckill.domain.OrderInfo;
import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.result.CodeMsg;
import com.ziliang.seckill.result.Result;
import com.ziliang.seckill.service.GoodsService;
import com.ziliang.seckill.service.OrderService;
import com.ziliang.seckill.service.SeckillUserService;
import com.ziliang.seckill.vo.GoodsVo;
import com.ziliang.seckill.vo.OrderDetailVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Api(value = "订单controller", tags = {"获取订单信息"})
@Controller
@CrossOrigin(origins = {"http://localhost:8081"}, allowCredentials = "true")
@RequestMapping("/order")
public class OrderController {
    @Autowired
    SeckillUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @ApiOperation(value = "获取订单详情")
    @ApiImplicitParam(name = "orderId", value = "订单id", required = true, dataType = "long")
    @RequestMapping("/detail/{orderId}")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, SeckillUser user,
                                      @PathVariable("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }
}
