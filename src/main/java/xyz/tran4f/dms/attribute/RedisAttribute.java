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
 * Redis 缓存键的前缀。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class RedisAttribute {

    private RedisAttribute() {
    }

    public static final String KEY_DRAFT = "KEY_DRAFT";
    public static final String KEY_NOTICE = "KEY_NOTICE";
    public static final String KEY_BUILDING_LIST = "KEY_RECORD_LIST";
    public static final String KEY_ACTIVE_TASK = "KEY_ACTIVE_TASK";
    public static final String KEY_ACTIVE_WEEK = "KEY_ACTIVE_WEEK";

    public static final String PREFIX_DIRTY_SET = "DIRTY_SET_";
    public static final String PREFIX_CLEAN_SET = "CLEAN_SET_";
    public static final String PREFIX_TASK_RECORD = "TASK_RECORD_";
    public static final String PREFIX_USER_LOCKED = "USER_LOCKED_";
    public static final String PREFIX_USER_CAPTCHA = "USER_CAPTCHA_";
    public static final String PREFIX_RESET_PASSWORD_ID = "RESET_PASSWORD_ID_";
    public static final String PREFIX_RESET_PASSWORD_KEY = "RESET_PASSWORD_KEY_";

}
