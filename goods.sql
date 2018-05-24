CREATE TABLE `goods` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `goods_name` VARCHAR(16) DEFAULT NULL COMMENT '商品名称',
  `goods_title` VARCHAR(64) DEFAULT NULL COMMENT '商品标题',
  `goods_img` VARCHAR(64) DEFAULT NULL COMMENT '商品的图片',
  `goods_detail` longtext COMMENT '商品的详情介绍',
  `goods_price` DECIMAL(10,2) DEFAULT 0.00 COMMENT '商品单价',
  `goods_stock` INT(11) DEFAULT 0 COMMENT '商品库存,-1表示没有限制',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

INSERT INTO `goods` VALUES (1,'iphoneX','Apple iphone X','/img/iphonex.png','64G 银色 三网通', 8765.00,10000),
                            (2,'mate10','huawei mate10','/img/mate10.png','4g+32g 三网通',3212.00,-1);