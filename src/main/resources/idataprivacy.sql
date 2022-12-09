/*
 Navicat Premium Data Transfer

 Source Server         : idataMask
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : idata

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 09/12/2022 17:37:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for livevideomask
-- ----------------------------
DROP TABLE IF EXISTS `livevideomask`;
CREATE TABLE `livevideomask`  (
  `exec_id` int NOT NULL AUTO_INCREMENT COMMENT '实时流任务id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `rule_id` int NULL DEFAULT NULL COMMENT '脱敏规则id',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '任务名称',
  `stream_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT 'RTMP流url',
  `out_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '转码文件地址/home/ysjs3/java/upfile/',
  `out_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '转码文件名',
  `data_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '数据名称',
  `obs_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '文件在obs的存储路径',
  `task_status` int UNSIGNED NOT NULL COMMENT '任务状态 0：python正在执行，1：python脚本执行失败，2：文件上传成功，3：文件上传失败，4：转码成功，5：转码失败，6：文件下载成功，7：文件下载失败',
  `use_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT 'device:cpu或者gpu',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏对象：person、plate等',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `is_type` int UNSIGNED NOT NULL DEFAULT 1 COMMENT '动态脱敏标志',
  `isdelete` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '0存在，1被删除',
  PRIMARY KEY (`exec_id`) USING BTREE,
  INDEX `live_task_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of livevideomask
-- ----------------------------
INSERT INTO `livevideomask` VALUES (1, 1, NULL, 'x', 'http://3891.liveplay.myqcloud.com/live/3891_user_63f0cabb_f7ac.flv', '/home/ysjs3/java/upfile/1/01/', 'test', NULL, '1/01/', 0, 'cpu', 'person,plate,', '2022-11-28 15:17:05', NULL, 1, 0);
INSERT INTO `livevideomask` VALUES (2, 1, NULL, 'x', 'http://3891.liveplay.myqcloud.com/live/3891_user_1664c4d1_9a1c.flv', '/home/ysjs3/java/upfile/1/2/', 'test', NULL, '1/2/', 1, 'cpu', 'person,plate,', '2022-11-28 15:26:59', '2022-11-28 15:27:10', 1, 0);
INSERT INTO `livevideomask` VALUES (3, 1, NULL, 's', 'http://3891.liveplay.myqcloud.com/live/3891_user_1664c4d1_9a1c.flv', '/home/ysjs3/java/upfile/1/3/', 'test', NULL, '1/3/', 7, 'cpu', 'person,plate,', '2022-11-28 15:37:26', '2022-11-28 15:41:16', 1, 0);
INSERT INTO `livevideomask` VALUES (4, 1, NULL, 's', 'http://3891.liveplay.myqcloud.com/live/3891_user_1664c4d1_9a1c.flv', '/home/ysjs3/java/upfile/1/4/', 'test', NULL, '1/4/', 7, 'cpu', 'person,plate,', '2022-11-28 15:55:49', '2022-11-28 15:58:43', 1, 0);
INSERT INTO `livevideomask` VALUES (5, 1, NULL, 'r', 'http://3891.liveplay.myqcloud.com/live/3891_user_1664c4d1_9a1c.flv', '/home/ysjs3/java/upfile/1/5/', 'liang', NULL, '1/5/', 7, 'cpu', 'person,plate,', '2022-11-28 16:25:08', '2022-11-28 16:27:58', 1, 0);
INSERT INTO `livevideomask` VALUES (6, 1, NULL, 'r', 'http://3891.liveplay.myqcloud.com/live/3891_user_1664c4d1_9a1c.flv', '/home/ysjs3/java/upfile/1/6/', 'liang', NULL, '1/6/', 6, 'cpu', 'person,plate,', '2022-11-28 16:31:54', '2022-11-28 16:34:48', 1, 0);
INSERT INTO `livevideomask` VALUES (7, 2, NULL, 'y', 'http://3891.liveplay.myqcloud.com/live/3891_user_1259f110_4587.flv', '/home/ysjs3/java/upfile/2/1/', 'youname', NULL, '2/1/', 1, 'cpu', 'person,plate,', '2022-11-29 15:55:57', '2022-11-29 15:55:57', 1, 0);
INSERT INTO `livevideomask` VALUES (8, 2, NULL, 'o', 'http://3891.liveplay.myqcloud.com/live/3891_user_1259f110_4587.flv', '/home/ysjs3/java/upfile/2/2/', 'youname', NULL, '2/2/', 6, 'cpu', 'person,plate,', '2022-11-29 16:00:47', '2022-11-29 16:03:31', 1, 0);
INSERT INTO `livevideomask` VALUES (9, 2, NULL, 't', 'http://3891.liveplay.myqcloud.com/live/3891_user_1259f110_4587.flv', '/home/ysjs3/java/upfile/2/3/', 'youname', NULL, '2/3/', 6, 'cpu', 'person,plate,', '2022-11-29 16:07:18', '2022-11-29 16:13:13', 1, 0);
INSERT INTO `livevideomask` VALUES (10, 2, NULL, 't', 'http://3891.liveplay.myqcloud.com/live/3891_user_5438c9eb_bdbc.flv', '/home/ysjs3/java/upfile/2/4/', 'youname', NULL, '2/4/', 0, 'cpu', 'person,plate,', '2022-11-29 16:15:34', NULL, 1, 0);
INSERT INTO `livevideomask` VALUES (11, 2, NULL, 'i', 'http://3891.liveplay.myqcloud.com/live/3891_user_cc8079a8_f7d2.flv', '/home/ysjs3/java/upfile/2/5/', 'youname', NULL, '2/5/', 6, 'cpu', 'person,plate,', '2022-11-29 16:39:23', '2022-11-29 16:46:21', 1, 0);
INSERT INTO `livevideomask` VALUES (12, 2, NULL, 'p', 'http://3891.liveplay.myqcloud.com/live/3891_user_d187259b_5f3f.flv', '/home/ysjs3/java/upfile/2/6/', 'youname', NULL, '2/6/', 6, 'cpu', 'person,plate,', '2022-12-07 15:17:06', '2022-12-07 15:20:49', 1, 0);
INSERT INTO `livevideomask` VALUES (13, 2, NULL, 'i', 'http://3891.liveplay.myqcloud.com/live/3891_user_d187259b_5f3f.flv', '/home/ysjs3/java/upfile/2/7/', 'youname', NULL, '2/7/', 6, 'cpu', 'person,plate,', '2022-12-07 15:56:01', '2022-12-07 15:56:37', 1, 0);

-- ----------------------------
-- Table structure for localmask
-- ----------------------------
DROP TABLE IF EXISTS `localmask`;
CREATE TABLE `localmask`  (
  `exec_id` int NOT NULL AUTO_INCREMENT COMMENT '本地脱敏id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `task_id` int NOT NULL COMMENT '任务id',
  `rule_id` int NULL DEFAULT NULL COMMENT '脱敏规则id',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '任务名称',
  `data_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '数据名称',
  `origin_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '待脱敏的文件路径：/home/ysjs3/java/upfile/test.mp4',
  `mask_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏后文件保存路径：/home/ysjs3/java/output/test.mp4',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏对象：person、plate等',
  `start_time` datetime NULL DEFAULT NULL COMMENT '任务开启时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '任务结束时间',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT 'device: cpu或 gpu',
  `task_status` int UNSIGNED NULL DEFAULT NULL COMMENT '0正在执行，1执行成功，2执行失败',
  `is_type` int(1) UNSIGNED ZEROFILL NOT NULL DEFAULT 0 COMMENT '静态脱敏标志位：默认为0',
  `isdelete` int(1) UNSIGNED ZEROFILL NULL DEFAULT NULL COMMENT '0存在，1被删除.',
  PRIMARY KEY (`exec_id`) USING BTREE,
  INDEX `local_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of localmask
-- ----------------------------
INSERT INTO `localmask` VALUES (1, 1, 0, NULL, 'y', NULL, '/home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-11-28 16:55:26', '2022-11-28 16:55:26', 'cpu', 2, 0, 1);
INSERT INTO `localmask` VALUES (2, 1, 0, NULL, 't', NULL, '/home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-11-28 16:55:57', '2022-11-28 16:55:57', 'cpu', 2, 0, 0);
INSERT INTO `localmask` VALUES (3, 1, 0, NULL, 'e', NULL, '/home/ysjs3/java/upfile/0198c25790ad81c091d8d0e5c850a0ed/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-12-07 15:12:27', '2022-12-07 15:13:37', 'cpu', 1, 0, 0);
INSERT INTO `localmask` VALUES (4, 1, 0, NULL, 'r', NULL, '/home/ysjs3/java/upfile/localvideo/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-12-07 15:29:34', '2022-12-07 15:30:42', 'cpu', 1, 0, 0);
INSERT INTO `localmask` VALUES (5, 1, 0, NULL, 'o', NULL, '/home/ysjs3/java/upfile/localvideo/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-12-07 16:46:21', '2022-12-07 16:46:21', 'cpu', 2, 0, 0);
INSERT INTO `localmask` VALUES (6, 1, 0, NULL, 'i', NULL, '/home/ysjs3/java/upfile/localvideo/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-12-07 16:50:20', '2022-12-07 16:50:20', 'cpu', 2, 0, 0);
INSERT INTO `localmask` VALUES (7, 1, 0, NULL, 'm', NULL, '/home/ysjs3/java/upfile/localvideo/person3.mp4', '/home/ysjs3/java/upfile/person3.mp4', 'person,plate,', '2022-12-07 16:51:53', '2022-12-07 16:53:01', 'cpu', 1, 0, 0);

-- ----------------------------
-- Table structure for maskdata
-- ----------------------------
DROP TABLE IF EXISTS `maskdata`;
CREATE TABLE `maskdata`  (
  `mask_data_id` int NOT NULL AUTO_INCREMENT COMMENT '脱敏后的数据id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `mask_id` int NULL DEFAULT NULL COMMENT '任务id',
  `exec_id` int NULL DEFAULT NULL COMMENT '执行任务id',
  `mask_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏后数据存放地址',
  `data_type` int NULL DEFAULT NULL COMMENT '数据类型 文本、表格、视频等',
  `is_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '0：静态,1：动态',
  `data_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '静态的话就是数据集名称，动态的就是url',
  `start_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `is_delete` int NULL DEFAULT NULL COMMENT '0:未删除,1删除',
  PRIMARY KEY (`mask_data_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of maskdata
-- ----------------------------

-- ----------------------------
-- Table structure for maskmethod
-- ----------------------------
DROP TABLE IF EXISTS `maskmethod`;
CREATE TABLE `maskmethod`  (
  `mask_method_id` int NOT NULL AUTO_INCREMENT COMMENT '脱敏方法id',
  `method_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏方法名称',
  PRIMARY KEY (`mask_method_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of maskmethod
-- ----------------------------

-- ----------------------------
-- Table structure for maskrule
-- ----------------------------
DROP TABLE IF EXISTS `maskrule`;
CREATE TABLE `maskrule`  (
  `rule_id` int NOT NULL AUTO_INCREMENT COMMENT '脱敏规则id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `rule_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏规则名称',
  `data_type` int UNSIGNED NULL DEFAULT NULL COMMENT '0:视频，1:文本，2:表格',
  `isupload` int UNSIGNED NULL DEFAULT NULL COMMENT '0：自定义，1：上传',
  `rule_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '上传规则的保存路径',
  `rule_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '规则描述',
  `limit_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '限制内容',
  `limit_form` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '限制形式',
  `rule_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '规则类型',
  `rule_resource` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '规则来源',
  `time` datetime NULL DEFAULT NULL COMMENT '添加或修改时间',
  `isdelete` int(1) UNSIGNED ZEROFILL NULL DEFAULT 0 COMMENT '0:未删除，1:删除',
  PRIMARY KEY (`rule_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of maskrule
-- ----------------------------
INSERT INTO `maskrule` VALUES (1, 1, '动规层办', 0, 15, 'est mollit labore cillum', 'do eiusmod dolore', 'enim ad voluptate consectetur', 'eiusmod', 'sit adipisicing deserunt mollit occaecat', 'in', NULL, 0);
INSERT INTO `maskrule` VALUES (2, 1, '热门效毛', 0, 79, 'veniam nostrud dolor dolore in', 'consectetur Duis', 'ea in aute', 'dolor sed Lorem veniam', 'commodo Excepteur laborum ullamco dolor', 'dolor exercitation', NULL, 0);
INSERT INTO `maskrule` VALUES (3, 1, '热门效毛', 0, 79, 'veniam nostrud dolor dolore in', 'consectetur Duis', 'ea in aute', 'dolor sed Lorem veniam', 'commodo Excepteur laborum ullamco dolor', 'dolor exercitation', NULL, 1);
INSERT INTO `maskrule` VALUES (4, 1, '热门效毛', 0, 79, 'veniam nostrud dolor dolore in', 'consectetur Duis', 'ea in aute', 'dolor sed Lorem veniam', 'commodo Excepteur laborum ullamco dolor', 'dolor exercitation', '2022-12-08 16:53:19', 0);
INSERT INTO `maskrule` VALUES (5, 14, '热门效毛', 86, 79, 'veniam nostrud dolor dolore in', 'consectetur Duis', 'ea in aute', 'dolor sed Lorem veniam', 'commodo Excepteur laborum ullamco dolor', 'dolor exercitation', '2022-12-09 15:07:14', 0);

-- ----------------------------
-- Table structure for masktask
-- ----------------------------
DROP TABLE IF EXISTS `masktask`;
CREATE TABLE `masktask`  (
  `task_id` int NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `rule_id` int NULL DEFAULT NULL COMMENT '规则绑定',
  `method_id` int NULL DEFAULT NULL COMMENT '脱敏方法',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '任务名称',
  `task_status` int NULL DEFAULT NULL COMMENT '任务状态',
  `mask_type` int NULL DEFAULT NULL COMMENT '脱敏类型',
  `task_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '任务描述',
  `data_id` int NULL DEFAULT NULL COMMENT '静态数据id',
  `data_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '静态文件位置',
  `data_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '数据类型 文本、表格、视频等',
  `stream_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '动态数据url',
  `method` int NULL DEFAULT NULL COMMENT '0:cpu,1:gpu',
  `time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of masktask
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  `company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Ma Lan', '5343 506393', 'Jiang Kee Company Limited');
INSERT INTO `user` VALUES (2, 'jiaxing', '5343 506393', 'jia xing');

SET FOREIGN_KEY_CHECKS = 1;
