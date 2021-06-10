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

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;

import java.util.Objects;

/**
 * 运行时异常的消息回显，是所有需要回显给用户的异常信息的父类。该异常会被全局异常解析器解析，
 * 回显错误的详细信息。如需使用 i18n 国际化消息，详细消息可使用国际化信息配置文件中的键值。
 *
 * @author 王帅
 * @since 1.0
 */
@Setter
@Getter
public abstract class AbstractMessageException extends RuntimeException {

    private static final long serialVersionUID = 8747711960406241674L;

    /**
     * 将为消息中的参数填充的参数数组，如果没有，则为 {@code null}。
     */
    private Object[] args;

    /**
     * 用指定的详细消息构造一个新的运行时异常。其中的详细消息用于回显到前端界面，
     * 详细消息的内容不应该为 {@code null}。
     *
     * @param message 需要回显的消息信息
     * @param args 使用国际化消息时可设定的参数
     * @exception NullPointerException 如果回显详细消息为 {@code null}
     */
    @Contract(value = "null,_ -> fail", pure = true)
    public AbstractMessageException(String message, Object... args) {
        super(Objects.requireNonNull(message));
        setArgs(args);
    }

}
