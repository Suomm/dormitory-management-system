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
 * 缺少相关属性信息时的异常。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class MissingAttributeException extends MessageException {

    private static final long serialVersionUID = 5119539187946629143L;

    /**
     * <p>
     * 用指定的详细消息构建一个 {@code MissingAttributeException} 实例。
     * </p>
     *
     * @param message 需要回显的消息信息
     */
    @Contract(value = "null -> fail", pure = true)
    public MissingAttributeException(String message) {
        super(message);
    }

}
