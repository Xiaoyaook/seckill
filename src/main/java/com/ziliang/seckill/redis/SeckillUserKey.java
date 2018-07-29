package com.ziliang.seckill.redis;

public class SeckillUserKey extends BasePrefix {
    /**
     * 用户登录的有效期：2天
     */
    public static final int TOKEN_EXPIRE = 3600*24 * 2;

    private SeckillUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 用户token前缀
     */
    public static SeckillUserKey token = new SeckillUserKey(TOKEN_EXPIRE, "tk");

    /**
     * 由id获取用户前缀
     */
    public static SeckillUserKey getById = new SeckillUserKey(0, "id");
}
