package com.ziliang.seckill.rabbitmq;

import com.ziliang.seckill.domain.SeckillUser;

/**
 * 定义消息队列所要传送的消息
 */
public class SeckillMessage {
    private SeckillUser user;
    private long goodsId;
    public SeckillUser getUser() {
        return user;
    }
    public void setUser(SeckillUser user) {
        this.user = user;
    }
    public long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}
