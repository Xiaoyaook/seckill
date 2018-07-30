CREATE TABLE `seckill_order` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) DEFAULT NULL COMMENT '用户ID',
  `order_id` BIGINT(20) DEFAULT NULL COMMENT '订单ID',
  `goods_id` BIGINT(20) DEFAULT NULL COMMENT '商品ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- 创建唯一索引,保证数据不重复
ALTER TABLE seckill_order ADD UNIQUE u_uid_gid (user_id,goods_id);