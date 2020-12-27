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

package xyz.tran4f.dms.exception;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

/**
 * <p>
 * 用于回显错误消息并重定向操作。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Getter
@Setter
public class RedirectException extends RuntimeMessageException {

    private static final long serialVersionUID = -36415506607708975L;

    private String redirectUrl;

    @Contract(pure = true)
    public RedirectException(String redirectUrl, MessageException cause) {
        super(cause.getMessage(), cause, cause.getArgs());
        setRedirectUrl(redirectUrl);
    }

}
