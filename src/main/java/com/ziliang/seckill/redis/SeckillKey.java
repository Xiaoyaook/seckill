package com.ziliang.seckill.redis;

public class SeckillKey extends BasePrefix {
    public SeckillKey(int expireSeconds,String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 秒杀结束的前缀
     */
    public static SeckillKey isGoodsOver = new SeckillKey(0, "go");
    /**
     * 秒杀地址的前缀
     */
    public static SeckillKey getSeckillPath = new SeckillKey(60, "sp");
    /**
     * 秒杀验证码的前缀
     */
    public static SeckillKey getSeckillVerifyCode = new SeckillKey(300, "vc");
}
