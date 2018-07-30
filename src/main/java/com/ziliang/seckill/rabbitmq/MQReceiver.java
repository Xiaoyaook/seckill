package com.ziliang.seckill.rabbitmq;

import com.rabbitmq.client.Channel;
import com.ziliang.seckill.domain.SeckillOrder;
import com.ziliang.seckill.domain.SeckillUser;
import com.ziliang.seckill.redis.RedisService;
import com.ziliang.seckill.service.GoodsService;
import com.ziliang.seckill.service.OrderService;
import com.ziliang.seckill.service.SeckillService;
import com.ziliang.seckill.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 消息接收者
 */
@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    /**
     * 消息队列，只接收消息内容
     *
     */
    @RabbitListener(queues=MQConfig.SECKILL_QUEUE)
    public void receive(String SeckillMsg, Message message, Channel channel) {
        log.info("receive message:"+SeckillMsg);
        SeckillMessage sm  = RedisService.stringToBean(SeckillMsg, SeckillMessage.class);
        SeckillUser user = sm.getUser();
        long goodsId = sm.getGoodsId();

        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock <= 0) {
            return;
        }
        //判断是否已经秒杀到了
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goods);

        // 确认消息已经消费成功
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);// 确认成功
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
