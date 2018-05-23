package com.ziliang.seckill.redis;


// 最顶端接口
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
