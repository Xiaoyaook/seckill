package com.ziliang.seckill.service;

import com.ziliang.seckill.vo.GoodsVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsServiceTest {

    @Autowired
    GoodsService goodsService;

    @Test
    public void testlistGoodsVo() {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        assertEquals(goodsVoList.size(),2);
    }

    @Test
    public void getGoodsVoByGoodsId() {

    }

    @Test
    public void reduceStock() {
    }

    @Test
    public void resetStock() {
    }
}