package com.ziliang.seckill.redis;

public class SeckillUserKey extends BasePrefix {
    public static final int TOKEN_EXPIRE = 3600*24 * 2;  // 有效期2天
    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");
}
