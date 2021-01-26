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
 * 异常详细消息国际化，属性对应国际化配置文件的键值。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class ExceptionAttribute {

    private ExceptionAttribute() {
    }

    public static final String MESSAGE_BAD_REQUEST = "exception.message.badRequest";
    public static final String USER_MESSAGE_FAIL_UPDATE = "exception.message.failUpdate";

    public static final String TASK_NONE = "exception.task.none";
    public static final String TASK_REPEAT = "exception.task.repeat";
    public static final String DOWNLOAD_FILE_NOT_FOUND = "exception.download.fileNotFound";
    public static final String USER_NOT_FOUND = "exception.user.notFound";
    public static final String USER_CAPTCHA_WRONG = "exception.user.captcha.wrong";
    public static final String USER_CHANGE_PASSWORD = "exception.user.changePassword";
    public static final String USER_CAPTCHA_OVERDUE = "exception.user.captcha.overdue";
    public static final String USER_CAPTCHA_MISSING = "exception.user.captcha.missing";
    public static final String USER_REGISTER_REPEAT = "exception.user.register.repeat";
    public static final String USER_RESET_PASSWORD_EXPIRE = "exception.user.resetPassword.expire";
    public static final String USER_RESET_PASSWORD_OVERDUE = "exception.user.resetPassword.overdue";
    public static final String USER_RESET_PASSWORD_INCOMPLETE = "exception.user.resetPassword.incomplete";

}
