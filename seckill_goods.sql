CREATE TABLE `seckill_goods` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '秒杀商品表',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品id',
  `seckill_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '秒杀价',
  `stock_count` INT(11) DEFAULT NULL COMMENT '库存数量',
  `start_date` DATETIME  DEFAULT NULL COMMENT '秒杀开始时间',
  `end_date` DATETIME  DEFAULT NULL COMMENT '秒杀结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

INSERT INTO `seckill_goods` VALUES (1,1,0.01,4,'2018-11-05 15:18:00','2018-11-13 14:00:18'),
                                  (2,2,0.01,9,'2018-11-12 15:18:00','2018-11-13 14:00:18')