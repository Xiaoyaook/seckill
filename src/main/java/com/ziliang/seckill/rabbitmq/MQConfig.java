package com.ziliang.seckill.rabbitmq;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ 配置类
 */
@Configuration
public class MQConfig {

    public static final String SECKILL_QUEUE = "seckill.queue";

    @Bean
    public Queue queue() {
        return new Queue(SECKILL_QUEUE, true);
    }

}
