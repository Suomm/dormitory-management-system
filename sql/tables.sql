/*
 * Copyright (C) 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (C) 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dormitory
-- ----------------------------
DROP TABLE IF EXISTS `dormitory`;
CREATE TABLE `dormitory`
(
    `grade`    char(5) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '年级',
    `building` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '楼号',
    `room`     varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '宿舍号',
    `category` int                                                    NOT NULL COMMENT '宿舍类型：0 女生宿舍 1 男生宿舍',
    `type`     int                                                    NOT NULL COMMENT '宿舍类别：1 本科生 0 研究生',
    PRIMARY KEY (`room`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for persistent_logins
-- ----------------------------
DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins`
(
    `username`  varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `series`    varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `token`     varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `last_used` timestamp                                              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`series`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for task
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task`
(
    `task_id`   int                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
    `grade`     varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL,
    `category`  int                                                    NULL     DEFAULT NULL,
    `type`      int                                                    NULL     DEFAULT NULL,
    `menu`      tinyint(1)                                             NULL     DEFAULT 0,
    `complete`  tinyint(1)                                             NULL     DEFAULT 0,
    `total`     int                                                    NULL     DEFAULT NULL,
    `menu_icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL,
    `parent_id` int                                                    NOT NULL DEFAULT -1,
    PRIMARY KEY (`task_id`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 147
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`
(
    `id`                      char(10) CHARACTER SET utf8 COLLATE utf8_general_ci                                                   NOT NULL COMMENT '学号',
    `name`                    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci                                                NOT NULL COMMENT '姓名',
    `password`                varchar(72) CHARACTER SET utf8 COLLATE utf8_general_ci                                                NOT NULL COMMENT '密码',
    `gender`                  int                                                                                                   NULL     DEFAULT NULL COMMENT '性别：0表示女，1表示男',
    `grade`                   varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci                                                NOT NULL COMMENT '年级',
    `email`                   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci                                               NOT NULL COMMENT '邮箱',
    `role`                    enum ('ROLE_ROOT','ROLE_USER','ROLE_MANAGER','ROLE_GUEST') CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT 'ROLE_USER' COMMENT '角色',
    `account_non_locked`      tinyint(1)                                                                                            NOT NULL DEFAULT 1 COMMENT '账户是否锁定',
    `credentials_non_expired` tinyint(1)                                                                                            NOT NULL DEFAULT 1 COMMENT '账户凭据是否过期',
    `account_non_expired`     tinyint(1)                                                                                            NOT NULL DEFAULT 1 COMMENT '账户是否过期',
    `enabled`                 tinyint(1)                                                                                            NULL     DEFAULT 1 COMMENT '账户是否可用',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
