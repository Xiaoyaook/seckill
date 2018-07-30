package com.ziliang.seckill.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ 配置类
 */
@Configuration
public class MQConfig implements InitializingBean {

    /**
     * 交换器名字
     */
    public static final String EXCHANGE_NAME = "exchange.log";

    /**
     * 队列名字
     */
    public static final String SECKILL_QUEUE = "seckill.queue";

    /**
     * 路由Key
     */
    public static final String ROUTING_KEY_MSG = "routing.msg.*";


    @Autowired
    private AmqpAdmin amqpAdmin;

    @Override
    public void afterPropertiesSet() throws Exception {
        DirectExchange exchange = new DirectExchange(EXCHANGE_NAME);
        Queue queue = new Queue(SECKILL_QUEUE, true);
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_MSG));
    }


}
