CREATE TABLE `order_info` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
  `delivery_addr_id` BIGINT(20) DEFAULT NULL COMMENT '收货地址ID',
  `goods_name` VARCHAR(16) DEFAULT NULL COMMENT '冗余过来的商品名称',
  `goods_count` INT(11) DEFAULT 0 COMMENT '商品数量',
  `goods_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品单价',
  `order_channel` TINYINT(4) DEFAULT 0 COMMENT '1pc,2android,3ios',
  `status` TINYINT(4) DEFAULT 0 COMMENT '订单状态, 0新建未支付, 1已支付,2已发货,3已收货,4已退款,5已完成',
  `create_date` DATETIME  DEFAULT NULL COMMENT '订单创建时间',
  `pay_date` DATETIME  DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4;