package com.ziliang.seckill.redis;

public class GoodsKey extends BasePrefix {

    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 商品列表的前缀
     */
    public static GoodsKey getGoodsList = new GoodsKey(60, "gl");
    /**
     * 商品详情的前缀
     */
    public static GoodsKey getGoodsDetail = new GoodsKey(60, "gd");
    /**
     * 秒杀商品库存的前缀
     */
    public static GoodsKey getSeckillGoodsStock= new GoodsKey(0, "gs");
}

