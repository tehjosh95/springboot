CREATE TABLE `game_sales` (
  `id` int NOT NULL AUTO_INCREMENT,
  `game_no` int(100) NOT NULL,
  `game_name` varchar(20) DEFAULT NULL,
  `game_code` varchar(5) DEFAULT NULL,
  `type` int(2) DEFAULT NULL,
  `cost_price` DECIMAL(10,2) DEFAULT NULL,
  `tax` DECIMAL(10,2) DEFAULT NULL,
  `sale_price` DECIMAL(10,2) DEFAULT NULL,
  `date_of_sale` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;