package com.ziliang.seckill.redis;

public class OrderKey extends BasePrefix {
    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
