package com.ziliang.seckill.redis;


/**
 * 抽象类,实现最顶端接口
 * 所有Key类都继承此类
 *
 */
public abstract class BasePrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix) {//0代表永不过期
        this(0, prefix);
    }

    public BasePrefix( int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * @return 返回过期时间，默认0代表永不过期
     */
    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    /**
     * @return 返回 当前类名：前缀
     */
    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+ ":" + prefix;
    }
}
