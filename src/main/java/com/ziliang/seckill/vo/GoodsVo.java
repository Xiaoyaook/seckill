package com.ziliang.seckill.vo;

import com.ziliang.seckill.domain.Goods;

import java.util.Date;


// 把秒杀商品的属性与普通商品属性整合
public class GoodsVo extends Goods {
    private Double SeckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Double getSeckillPrice() {
        return SeckillPrice;
    }

    public void setSeckillPrice(Double seckillPrice) {
        SeckillPrice = seckillPrice;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
