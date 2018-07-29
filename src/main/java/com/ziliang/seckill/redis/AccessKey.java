package com.ziliang.seckill.redis;


public class AccessKey extends BasePrefix {
    private AccessKey( int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 接口限流时，存储访问次数所用的前缀，这里的过期时间就是我们接口限流的所设定的时间
     */
    public static AccessKey withExpire(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}
