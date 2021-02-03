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

package xyz.tran4f.dms.attribute;

/**
 * <p>
 * Redis 缓存的键。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class RedisAttribute {

    private RedisAttribute() {
    }

    // 常用的键值

    /**
     * <p>
     * 新闻稿的段落，是一个 {@code List<String>} 集合。
     * </p>
     */
    public static final String KEY_DRAFT = "KEY_DRAFT";

    /**
     * <p>
     * 当前周任务菜单 ID，是一个 {@code Integer} 类型的数字。
     * </p>
     */
    public static final String KEY_TASK_ID = "KEY_TASK_ID";

    /**
     * <p>
     * 公告信息，是一个 Redis Set 集合，以公告的创建日期为依据判断元素是否重复。
     * </p>
     */
    public static final String KEY_NOTICE = "KEY_NOTICE";

    /**
     * <p>
     * 部门邀请码，是一个 {@code String} 类型的字符串，在项目启动时被创建刷新，
     * 并在每天凌晨十二点刷新它的值。
     * </p>
     */
    public static final String KEY_CAPTCHA = "KEY_CAPTCHA";

    /**
     * <p>
     * 任务完成之后整理任务时产生的警告，是一个 Redis List 集合。
     * </p>
     */
    public static final String KEY_WARNINGS = "KEY_WARNINGS";

    /**
     * <p>
     * 当前周任务的所有任务 ID，是一个 Redis Set 集合。当有任务完成时从中删除任务 ID，直到
     * 剩余最后一个元素即为任务菜单，表示当前周任务完成。回滚任务时再次插入任务菜单ID 和具体
     * 任务 ID 保证数据的不重复。
     * </p>
     */
    public static final String KEY_ACTIVE_TASK = "KEY_ACTIVE_TASK";

    /**
     * <p>
     * 当前任务周数，是一个 {@code String} 类型的字符串。
     * </p>
     */
    public static final String KEY_ACTIVE_WEEK = "KEY_ACTIVE_WEEK";

    /**
     * <p>
     * 学期开始日期，用于计算任务周数，是一个 {@code Long} 类型。
     * </p>
     */
    public static final String KEY_SEMESTER_BEGIN = "KEY_SEMESTER_BEGIN";

    /**
     * <p>
     * 检查完一栋宿舍楼后根据成绩筛选出来的脏乱宿舍，存放在 Redis Hash 中。
     * </p>
     */
    public static final String KEY_DIRTY = "KEY_DIRTY";

    /**
     * <p>
     * 检查完一栋宿舍楼后根据成绩筛选出来的优秀宿舍，存放在 Redis Hash 中。
     * </p>
     */
    public static final String KEY_CLEAN = "KEY_CLEAN";

    /**
     * <p>
     * 所有宿舍楼宿舍成绩集合，存放在 Redis Hash 中。
     * </p>
     */
    public static final String KEY_TASK_RECORD = "KEY_TASK_RECORD";

    // 常用的键前缀

    /**
     * <p>
     * 用户是否被锁定前缀，以学号作为后缀标识。
     * </p>
     */
    public static final String PREFIX_USER_LOCKED = "USER_LOCKED_";

    /**
     * <p>
     * 用户的邮箱验证码，以学号作为后缀标识。
     * </p>
     */
    public static final String PREFIX_USER_CAPTCHA = "USER_CAPTCHA_";

    /**
     * <p>
     * 用户忘记密码时找回密码的前缀，以学号作为后缀标识。
     * </p>
     */
    public static final String PREFIX_RESET_PASSWORD = "RESET_PASSWORD_";

}
