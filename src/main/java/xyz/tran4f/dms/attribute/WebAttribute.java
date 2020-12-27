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
 * 管理 WEB 程序域中的属性。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class WebAttribute {

    private WebAttribute() {
    }

    public static final String WEB_LAST_EXCEPTION = "WEB_LAST_EXCEPTION";
    public static final String WEB_SESSION_USER = "WEB_SESSION_USER";
    public static final String WEB_SESSION_CAPTCHA = "WEB_SESSION_CAPTCHA";

}
