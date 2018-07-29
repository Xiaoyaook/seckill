package com.ziliang.seckill.redis;


/**
 * 最顶端接口
 * 所有Key都需要的两个方法
 */
public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
