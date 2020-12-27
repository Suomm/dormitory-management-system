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
 * 异常的消息信息回显，是所有需要回显给用户的异常信息的父类。如需使用 i18n 国际化消息，详细消息可使用
 * 国际化信息配置文件中的键值，可用的键值请参见 {@link xyz.tran4f.dms.attribute.ExceptionAttribute}
 * </p>
 * <p>
 * 该类异常不会被全局异常解析器解析，所以不会进行页面的跳转，返回出现异常之前的界面。建议出现该异常之后
 * 使用 {@link RedirectException} 进行页面的重定向操作，可以在其他页面进行详细消息回显。
 * <blockquote><pre>
 * try {
 *     // 会抛出该异常的逻辑代码
 * } catch (MessageException e) {
 *     throw new RedirectException("[URL 地址]", e);
 * }
 * </pre></blockquote>
 * 以上代码用于处理抛出异常后重定向到一个页面，而不是返回到原先请求的页面。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Setter
@Getter
public class MessageException extends Exception {

    private static final long serialVersionUID = 8747711960406241674L;

    private Object[] args;

    /**
     * <p>
     * 用指定的详细消息构造一个新的异常。其中的详细消息用于回显到前端界面，
     * 详细消息的内容不应该为 {@code null}。
     * </p>
     *
     * @param message 需要回显的消息信息
     * @param args 使用国际化消息时可设定的参数
     */
    @Contract(pure = true)
    public MessageException(String message, Object... args) {
        super(message);
        setArgs(args);
    }

}
