/*
 Navicat Premium Data Transfer

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 127.0.0.1:3306
 Source Schema         : sentinel-dashboard

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 07/11/2022 09:48:16
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_definition_entity
-- ----------------------------
DROP TABLE IF EXISTS `api_definition_entity`;
CREATE TABLE `api_definition_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `api_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for authority_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `authority_rule_entity`;
CREATE TABLE `authority_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for degrade_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `degrade_rule_entity`;
CREATE TABLE `degrade_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `resource` varchar(255) DEFAULT NULL,
  `limit_app` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `count` double DEFAULT NULL,
  `time_window` int DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `min_request_amount` int DEFAULT NULL,
  `slow_ratio_threshold` double DEFAULT NULL,
  `stat_interval_ms` int DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for flow_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `flow_rule_entity`;
CREATE TABLE `flow_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `limit_app` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  `resource` varchar(255) DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `count` double DEFAULT NULL,
  `strategy` int DEFAULT NULL,
  `ref_resource` varchar(255) DEFAULT NULL,
  `control_behavior` int DEFAULT NULL,
  `warm_up_period_sec` int DEFAULT NULL,
  `max_queueing_time_ms` int DEFAULT NULL,
  `cluster_mode` tinyint DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for gateway_flow_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `gateway_flow_rule_entity`;
CREATE TABLE `gateway_flow_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `resource` varchar(255) DEFAULT NULL,
  `resource_mode` int DEFAULT NULL,
  `grade` int DEFAULT NULL,
  `count` double DEFAULT NULL,
  `interval` bigint DEFAULT NULL,
  `interval_unit` int DEFAULT NULL,
  `control_behavior` int DEFAULT NULL,
  `burst` int DEFAULT NULL,
  `max_queueing_timeout_ms` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for metric_entity
-- ----------------------------
DROP TABLE IF EXISTS `metric_entity`;
CREATE TABLE `metric_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `resource` varchar(255) DEFAULT NULL COMMENT '资源名称',
  `timestamp` datetime DEFAULT NULL COMMENT '监控信息时间戳',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  `pass_qps` bigint DEFAULT NULL COMMENT '通过QPS',
  `success_qps` bigint DEFAULT NULL COMMENT '成功QPS',
  `block_qps` bigint DEFAULT NULL COMMENT '限流QPS',
  `exception_qps` bigint DEFAULT NULL COMMENT '异常QPS',
  `rt` decimal(10,2) DEFAULT NULL COMMENT '资源的平均响应时间',
  `count` int DEFAULT NULL COMMENT '本次聚合的总条数',
  `resource_code` int DEFAULT NULL COMMENT '资源hashcode',
  PRIMARY KEY (`id`),
  KEY `idx_app_timestamp` (`app`,`timestamp`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Sentinel监控信息表';

-- ----------------------------
-- Table structure for param_flow_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `param_flow_rule_entity`;
CREATE TABLE `param_flow_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for system_rule_entity
-- ----------------------------
DROP TABLE IF EXISTS `system_rule_entity`;
CREATE TABLE `system_rule_entity` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int DEFAULT NULL,
  `highest_system_load` double DEFAULT NULL,
  `avg_rt` bigint DEFAULT NULL,
  `max_thread` bigint DEFAULT NULL,
  `qps` double DEFAULT NULL,
  `highest_cpu_usage` double DEFAULT NULL,
  `gmt_create` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;
