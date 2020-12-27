/*
 * Copyright (C) 2020 Wang Shuai (suomm.macher@foxmail.com)
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
    public static final String MESSAGE_MISSING_ATTRIBUTE = "exception.message.missingAttribute";

    public static final String USER_NOT_FOUND = "exception.user.notFound";
    public static final String USER_REGISTER_REPEAT = "exception.user.register.repeat";
    public static final String USER_REGISTER_FAIL_INSERT = "exception.user.register.failInsert";
    public static final String USER_REGISTER_CAPTCHA_EXIST = "exception.user.register.captchaExist";
    public static final String USER_EMAIL_FAIL = "exception.user.email.fail";
    public static final String USER_MISSING_CAPTCHA = "exception.user.missingCaptcha";
    public static final String USER_RESET_PASSWORD_FAIL_UPDATE = "exception.user.resetPassword.failUpdate";
    public static final String USER_RESET_PASSWORD_MISSING_ARGUMENT = "exception.user.resetPassword.missingArgument";
    public static final String USER_RESET_PASSWORD_EXPIRE = "exception.user.resetPassword.expire";
    public static final String USER_RESET_PASSWORD_INCOMPLETE = "exception.user.resetPassword.incomplete";

}
