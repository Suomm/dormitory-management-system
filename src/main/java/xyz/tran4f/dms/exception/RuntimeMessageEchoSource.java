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
import org.jetbrains.annotations.NotNull;

/**
 * <p>
 * 消息回显时的数据源，是所有需要回显给用户的异常信息的父类。如需使用 i18n 国际化消息，
 * 详细消息可使用国际化信息配置文件中的键值，可用的键值请参见
 * {@link xyz.tran4f.dms.attribute.ExceptionAttribute}
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Setter
@Getter
public class RuntimeMessageEchoSource extends RuntimeException {

    private static final long serialVersionUID = 8747711960406241674L;

    private Object[] args;

    /**
     * <p>
     * 用指定的详细消息构造一个新的运行时异常。其中的详细消息用于回显到前端界面，
     * 详细消息的内容不应该为 {@code null}。
     * </p>
     *
     * @param message 需要回显的消息信息
     * @param args 使用国际化消息时可设定的参数
     */
    public RuntimeMessageEchoSource(@NotNull String message, Object... args) {
        super(message);
        setArgs(args);
    }

    /**
     * <p>
     * 用指定的详细消息和原因构造一个新的运行时异常。其中的详细消息用于回显到前端界面，
     * 详细消息的内容不应该为 {@code null}。
     * </p>
     *
     * @param message 需要回显的消息信息
     * @param cause 触发该异常的原因
     * @param args 使用国际化消息时可设定的参数
     */
    public RuntimeMessageEchoSource(@NotNull String message, Throwable cause, Object... args) {
        super(message, cause);
        setArgs(args);
    }

}
