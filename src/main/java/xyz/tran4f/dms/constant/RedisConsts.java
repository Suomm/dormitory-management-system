/*
 * Copyright (C) 2020-2021 the original author or authors.
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

package xyz.tran4f.dms.constant;

/**
 * Redis 缓存的键或键的前缀。
 *
 * @author 王帅
 * @since 1.0
 */
public final class RedisConsts {

    private RedisConsts() {
    }

    // 常用的键值

    /**
     * 新闻稿的段落，是一个 {@code List<String>} 集合。
     */
    public static final String KEY_DRAFT = "KEY_DRAFT";

    /**
     * 当前周任务菜单 ID，是一个 {@code Integer} 类型的数字。
     */
    public static final String KEY_TASK_ID = "KEY_TASK_ID";

    /**
     * 公告信息，是一个 Redis Hash 集合，以公告的创建日期为依据判断元素是否重复。
     */
    public static final String KEY_NOTICE = "KEY_NOTICE";

    /**
     * 部门邀请码，是一个 {@code String} 类型的字符串，在项目启动时被创建，
     * 并在每天凌晨十二点刷新它的值。
     */
    public static final String KEY_CAPTCHA = "KEY_CAPTCHA";

    /**
     * 任务完成之后整理任务时产生的警告，是一个 Redis List 集合。
     */
    public static final String KEY_WARNINGS = "KEY_WARNINGS";

    /**
     * 当前周任务的所有具体任务 ID，是一个 Redis Set 集合。当有任务完成时从中删除任务 ID，
     * 直到集合为空表示当前周的所有任务已完成。回滚任务时再次插入任务菜单 ID 保证数据的不重复。
     */
    public static final String KEY_ACTIVE_TASK = "KEY_ACTIVE_TASK";

    /**
     * 当前任务周数，是一个 {@code String} 类型的字符串。
     */
    public static final String KEY_ACTIVE_WEEK = "KEY_ACTIVE_WEEK";

    /**
     * 学期开始日期，用于计算任务周数，是一个 {@code Long} 类型。
     */
    public static final String KEY_SEMESTER_BEGIN = "KEY_SEMESTER_BEGIN";

    /**
     * 检查完一栋宿舍楼后根据成绩筛选出来的脏乱宿舍，存放在 Redis Hash 中。
     */
    public static final String KEY_DIRTY = "KEY_DIRTY";

    /**
     * 检查完一栋宿舍楼后根据成绩筛选出来的优秀宿舍，存放在 Redis Hash 中。
     */
    public static final String KEY_CLEAN = "KEY_CLEAN";

    /**
     * 所有宿舍楼宿舍成绩集合，存放在 Redis Hash 中。
     */
    public static final String KEY_TASK_RECORD = "KEY_TASK_RECORD";

    // 常用的键前缀

    /**
     * 用户是否被锁定前缀，以学号作为后缀标识。
     */
    public static final String PREFIX_USER_LOCKED = "USER_LOCKED_";

    /**
     * 用户的邮箱验证码，以学号作为后缀标识。
     */
    public static final String PREFIX_USER_CAPTCHA = "USER_CAPTCHA_";

    /**
     * 用户忘记密码时找回密码的前缀，以学号作为后缀标识。
     */
    public static final String PREFIX_RESET_PASSWORD = "RESET_PASSWORD_";

}
