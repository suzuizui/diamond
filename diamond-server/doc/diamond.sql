/*
Navicat MySQL Data Transfer

Target Server Type    : MYSQL
Target Server Version : 50505
File Encoding         : 65001

Date: 2017-07-31 15:22:00
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for config_info
-- ----------------------------
DROP TABLE IF EXISTS `config_info`;
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) DEFAULT NULL,
  `group_id` varchar(255) DEFAULT NULL,
  `datum_id` varchar(255) DEFAULT NULL,
  `content` varchar(5000) DEFAULT NULL,
  `src_ip` varchar(32) DEFAULT NULL,
  `src_user` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_d_g` (`data_id`,`group_id`) USING BTREE,
  KEY `i_g` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=199 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for config_info_aggr
-- ----------------------------
DROP TABLE IF EXISTS `config_info_aggr`;
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL,
  `data_id` varchar(32) DEFAULT NULL,
  `group_id` varchar(32) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for config_info_copy
-- ----------------------------
DROP TABLE IF EXISTS `config_info_copy`;
CREATE TABLE `config_info_copy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) DEFAULT NULL,
  `group_id` varchar(255) DEFAULT NULL,
  `datum_id` varchar(255) DEFAULT NULL,
  `content` varchar(5000) DEFAULT NULL,
  `src_ip` varchar(32) DEFAULT NULL,
  `src_user` varchar(32) DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_d_g` (`data_id`,`group_id`) USING BTREE,
  KEY `i_g` (`group_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=190 DEFAULT CHARSET=utf8;
