# Sentinel 控制台

## 0. 概述

Sentinel 控制台是流量控制、熔断降级规则统一配置和管理的入口，它为用户提供了机器自发现、簇点链路自发现、监控、规则配置等功能。在 Sentinel 控制台上，我们可以配置规则并实时查看流量控制效果。

## 1. docker启动


### 1.1 建表


```bash
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

```

## 2. 启动

```bash
docker run -d \
-e JVM_XMS=256m \
-e JVM_XMX=256m \
-e JVM_XMN=256m \
-p 18080:8080 \
-e TZ=Asia/Shanghai \
-e DB_ADDRESS=192.168.0.7:3306 \
-e DB_NAME=sentinel-dashboard \
-e DB_USER=root \
-e DB_PASSWORD=root \
--network mynet \
--name sentinel-dashboard \
--restart=always \
zaizhuzhu123/sentinel-dashboard:1.3.1
```


## 3. 验证是否接入成功

客户端正确配置并启动后，会**在初次调用后**主动向控制台发送心跳包，汇报自己的存在；
控制台收到客户端心跳包之后，会在左侧导航栏中显示该客户端信息。如果控制台能够看到客户端的机器信息，则表明客户端接入成功了。

更多：[控制台功能介绍](./Sentinel_Dashboard_Feature.md)。