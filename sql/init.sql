-- 个人健康与运动数据中心 - 数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS health_fitness DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE health_fitness;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
  `age` INT DEFAULT NULL COMMENT '年龄',
  `height` DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
  `weight` DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 健康记录表（每日健康数据）
DROP TABLE IF EXISTS `health_record`;
CREATE TABLE `health_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `record_date` DATE NOT NULL COMMENT '记录日期',
  `steps` INT DEFAULT 0 COMMENT '步数',
  `calories` INT DEFAULT 0 COMMENT '消耗卡路里(kcal)',
  `distance` DECIMAL(6,2) DEFAULT 0 COMMENT '运动距离(km)',
  `heart_rate` INT DEFAULT NULL COMMENT '平均心率(bpm)',
  `sleep_duration` DECIMAL(4,1) DEFAULT NULL COMMENT '睡眠时长(小时)',
  `sleep_quality` TINYINT DEFAULT NULL COMMENT '睡眠质量：1差 2一般 3好 4很好',
  `weight` DECIMAL(5,1) DEFAULT NULL COMMENT '当日体重(kg)',
  `water_intake` INT DEFAULT 0 COMMENT '饮水量(ml)',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_date` (`user_id`, `record_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康记录表';

-- 运动记录表
DROP TABLE IF EXISTS `exercise_record`;
CREATE TABLE `exercise_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `exercise_type` VARCHAR(30) NOT NULL COMMENT '运动类型：running跑步 cycling骑行 swimming游泳 walking步行 yoga瑜伽 gym健身 other其他',
  `exercise_date` DATE NOT NULL COMMENT '运动日期',
  `duration` INT NOT NULL COMMENT '运动时长(分钟)',
  `distance` DECIMAL(6,2) DEFAULT 0 COMMENT '运动距离(km)',
  `calories` INT DEFAULT 0 COMMENT '消耗卡路里(kcal)',
  `heart_rate_avg` INT DEFAULT NULL COMMENT '平均心率',
  `heart_rate_max` INT DEFAULT NULL COMMENT '最大心率',
  `intensity` TINYINT DEFAULT 2 COMMENT '强度：1轻松 2中等 3高强度',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '运动感受/备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_date` (`user_id`, `exercise_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运动记录表';

-- 健康目标表
DROP TABLE IF EXISTS `health_goal`;
CREATE TABLE `health_goal` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `goal_type` VARCHAR(30) NOT NULL COMMENT '目标类型：steps每日步数 calories每日卡路里 distance每日距离 sleep每日睡眠 weight目标体重 exercise每周运动次数',
  `target_value` DECIMAL(10,2) NOT NULL COMMENT '目标值',
  `current_value` DECIMAL(10,2) DEFAULT 0 COMMENT '当前值',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `start_date` DATE DEFAULT NULL COMMENT '开始日期',
  `end_date` DATE DEFAULT NULL COMMENT '结束日期',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1进行中 2已完成 3已取消',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康目标表';

-- 初始测试用户（密码：123456，BCrypt加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `gender`, `age`, `height`, `weight`, `phone`) VALUES
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 1, 25, 175.0, 70.0, '13800138000');

-- 插入一些示例健康数据（最近7天）
INSERT INTO `health_record` (`user_id`, `record_date`, `steps`, `calories`, `distance`, `heart_rate`, `sleep_duration`, `sleep_quality`, `weight`, `water_intake`) VALUES
(1, DATE_SUB(CURDATE(), INTERVAL 6 DAY), 8234, 420, 5.6, 72, 7.5, 3, 70.2, 1800),
(1, DATE_SUB(CURDATE(), INTERVAL 5 DAY), 10567, 510, 7.2, 75, 6.8, 2, 70.1, 2000),
(1, DATE_SUB(CURDATE(), INTERVAL 4 DAY), 6789, 350, 4.5, 70, 8.0, 4, 70.0, 1500),
(1, DATE_SUB(CURDATE(), INTERVAL 3 DAY), 12345, 620, 8.4, 78, 7.2, 3, 69.9, 2200),
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 9876, 480, 6.7, 73, 7.8, 4, 69.8, 1900),
(1, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 11234, 560, 7.6, 76, 6.5, 2, 69.9, 2100),
(1, CURDATE(), 5432, 280, 3.8, 71, NULL, NULL, 70.0, 1200);

-- 插入示例运动记录
INSERT INTO `exercise_record` (`user_id`, `exercise_type`, `exercise_date`, `duration`, `distance`, `calories`, `heart_rate_avg`, `heart_rate_max`, `intensity`, `remark`) VALUES
(1, 'running', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 35, 5.0, 350, 145, 172, 2, '晨跑感觉不错'),
(1, 'cycling', DATE_SUB(CURDATE(), INTERVAL 4 DAY), 60, 15.0, 420, 130, 158, 2, '周末骑行'),
(1, 'running', DATE_SUB(CURDATE(), INTERVAL 3 DAY), 40, 6.0, 400, 150, 178, 3, '强度较大'),
(1, 'yoga', DATE_SUB(CURDATE(), INTERVAL 2 DAY), 45, 0, 180, 95, 110, 1, '放松身心'),
(1, 'swimming', DATE_SUB(CURDATE(), INTERVAL 1 DAY), 50, 1.5, 450, 135, 160, 2, '游泳训练');

-- 插入示例目标
INSERT INTO `health_goal` (`user_id`, `goal_type`, `target_value`, `current_value`, `unit`, `start_date`, `end_date`, `status`) VALUES
(1, 'steps', 10000, 8234, '步', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1),
(1, 'calories', 500, 420, 'kcal', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1),
(1, 'sleep', 8.0, 7.5, '小时', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 1 DAY), 1),
(1, 'weight', 68.0, 70.0, 'kg', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 1),
(1, 'exercise', 5, 3, '次/周', DATE_SUB(CURDATE(), INTERVAL 7 DAY), CURDATE(), 1);
