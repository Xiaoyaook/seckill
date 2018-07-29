package com.ziliang.seckill.util;

import java.util.UUID;

/**
 * 生成随机UUID
 */
public class UUIDUtil {
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
