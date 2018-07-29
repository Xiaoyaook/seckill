package com.ziliang.seckill.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(String prefix) {
        super(prefix);
    }

    /**
     * 通过用户id和商品id获取秒杀订单时，Redis Key的前缀
     */
    public static OrderKey getSeckillOrderByUidGid = new OrderKey("soug");
}
