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

package xyz.tran4f.dms.exception;

import org.jetbrains.annotations.Contract;

/**
 * <p>
 * 检测到用户不存在时抛出此异常。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class UserNotFoundException extends MessageException {

    private static final long serialVersionUID = -8639444066340783164L;

    /**
     * <p>
     * 用指定的详细消息构建一个 {@code UserNotFoundException} 实例。
     * </p>
     *
     * @param message 需要回显的消息信息
     * @param args 使用国际化消息时可设定的参数
     */
    @Contract(value = "null,_ -> fail", pure = true)
    public UserNotFoundException(String message, Object... args) {
        super(message, args);
    }

}
