package com.ziliang.seckill.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(String prefix) {
        super(prefix);
    }
    // seckill order userid goodsid
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("soug");
}
