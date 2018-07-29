package com.ziliang.seckill.service;

import com.ziliang.seckill.dao.GoodsDao;
import com.ziliang.seckill.domain.SeckillGoods;
import com.ziliang.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    /**
     * 把普通商品的属性和商品的秒杀属性整合起来，返回GoodsVo列表
     * @return List<GoodsVo>
     */
    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    /**
     * 通过商品id获取GoodsVo
     * @param goodsId 商品id
     * @return
     */
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    /**
     * 减库存
     * 减少可能会失败,所以返回一个布尔值
     *
     * @param goods GoodsVo
     * @return
     */
    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods g = new SeckillGoods();
        g.setGoodsId(goods.getId());
        int ret = goodsDao.reduceStock(g);
        return ret > 0;
    }

    /**
     * 重置库存
     * @param goodsList
     */
    public void resetStock(List<GoodsVo> goodsList) {
        for(GoodsVo goods : goodsList ) {
            SeckillGoods g = new SeckillGoods();
            g.setGoodsId(goods.getId());
            g.setStockCount(goods.getStockCount());
            goodsDao.resetStock(g);
        }
    }
}
