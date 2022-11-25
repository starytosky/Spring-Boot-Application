/*
 Navicat Premium Data Transfer

 Source Server         : idataMask
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : idatamask

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 25/11/2022 17:06:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for livevideomask
-- ----------------------------
DROP TABLE IF EXISTS `livevideomask`;
CREATE TABLE `livevideomask`  (
  `live_task_id` int NOT NULL COMMENT '实时流任务id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `stream_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT 'RTMP流url',
  `out_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '转码文件地址/home/ysjs3/java/upfile/',
  `out_filename` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '转码文件名',
  `task_status` int NULL DEFAULT NULL COMMENT '任务状态 0：python正在执行，1：python脚本执行失败，2：文件上传成功，3：文件上传失败，4：转码成功，5：转码失败，6：文件下载成功，7：文件下载失败',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏对象：person、plate等',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `isdelete` int NULL DEFAULT NULL COMMENT '0存在，1被删除',
  PRIMARY KEY (`live_task_id`) USING BTREE,
  INDEX `live_task_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `live_task_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of livevideomask
-- ----------------------------

-- ----------------------------
-- Table structure for localvideomask
-- ----------------------------
DROP TABLE IF EXISTS `localvideomask`;
CREATE TABLE `localvideomask`  (
  `local_task_id` int NOT NULL COMMENT '本地脱敏id',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `video_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '待脱敏的文件路径：/home/ysjs3/java/upfile/test.mp4',
  `mask_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏后文件保存路径：/home/ysjs3/java/output/test.mp4',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL COMMENT '脱敏对象：person、plate等',
  `start_time` datetime NULL DEFAULT NULL COMMENT '任务开启时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '任务结束时间',
  `task_status` int NULL DEFAULT NULL COMMENT '0正在执行，1执行成功，2执行失败',
  `isdelete` int NULL DEFAULT NULL COMMENT '0存在，1被删除',
  PRIMARY KEY (`local_task_id`) USING BTREE,
  INDEX `local_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `local_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of localvideomask
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  `company` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_520_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'Ma Lan', '(20) 4350 2927', 'Leung\'s Communications Limited');
INSERT INTO `user` VALUES (2, 'Jiang Anqi', '5343 506393', 'Jiang Kee Company Limited');
INSERT INTO `user` VALUES (3, 'Melissa Owens', '52-984-0128', 'Xue Kee Company Limited');
INSERT INTO `user` VALUES (4, 'Ishii Hikaru', '(161) 541 3732', 'Thomas Inc.');
INSERT INTO `user` VALUES (5, 'Deng Rui', '66-755-0539', 'Jialun Pharmaceutical Company Limited');

SET FOREIGN_KEY_CHECKS = 1;
