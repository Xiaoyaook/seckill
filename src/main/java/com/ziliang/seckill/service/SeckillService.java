package com.ziliang.seckill.service;

import com.ziliang.seckill.domain.OrderInfo;
import com.ziliang.seckill.domain.SeckillOrder;
import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.redis.SeckillKey;
import com.ziliang.seckill.util.MD5Util;
import com.ziliang.seckill.util.UUIDUtil;
import com.ziliang.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeckillService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo seckill(SeckillUser user, GoodsVo goods) {
        //减库存 下订单 写入秒杀订单
        boolean success = goodsService.reduceStock(goods);
        if(success) {
            //order_info seckill_order
            return orderService.createOrder(user, goods);
        }else {
            setGoodsOver(goods.getId()); // goods is over
            return null;
        }
    }

    public long getSeckillResult(Long userId, long goodsId) {
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(userId, goodsId);
        if(order != null) {//秒杀成功
            return order.getOrderId();
        }else {
            boolean isOver = getGoodsOver(goodsId);
            if(isOver) {
                return -1;
            }else {
                return 0;
            }
        }
    }

    private void setGoodsOver(Long goodsId) {
        redisService.set(SeckillKey.isGoodsOver, ""+goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(SeckillKey.isGoodsOver, ""+goodsId);
    }

    public void reset(List<GoodsVo> goodsList) {
        goodsService.resetStock(goodsList);
        orderService.deleteOrders();
    }

    public boolean checkPath(SeckillUser user, long goodsId, String path) {
        if(user == null || path == null) {
            return false;
        }
        String pathOld = redisService.get(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, String.class);
        return path.equals(pathOld);
    }

    public String createSeckillPath(SeckillUser user, long goodsId) {
        if(user == null || goodsId <=0) {
            return null;
        }
        String str = MD5Util.md5(UUIDUtil.uuid()+"123456");
        redisService.set(SeckillKey.getSeckillPath, ""+user.getId() + "_"+ goodsId, str);
        return str;
    }
}
