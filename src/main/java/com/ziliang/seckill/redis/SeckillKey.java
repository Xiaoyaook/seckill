package com.ziliang.seckill.redis;

public class SeckillKey extends BasePrefix {
    public SeckillKey(String prefix) {
        super(prefix);
    }

    public static SeckillKey isGoodsOver = new SeckillKey("go");
}
