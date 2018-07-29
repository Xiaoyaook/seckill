package com.ziliang.seckill.access;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    /**
     * 限流时间
     */
    int seconds();

    /**
     * 在限流时间内最多访问的次数
     */
    int maxCount();

    /**
     * 是否需要登录
     */
    boolean needLogin() default true;
}
