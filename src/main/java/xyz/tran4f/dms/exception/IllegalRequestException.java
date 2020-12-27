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

import org.jetbrains.annotations.Contract;
import xyz.tran4f.dms.attribute.ExceptionAttribute;

/**
 * <p>
 * 恶意绕过前端校验时，抛出此异常。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class IllegalRequestException extends RuntimeMessageException {

    private static final long serialVersionUID = 6723413299262506626L;

    @Contract(pure = true)
    public IllegalRequestException() {
        super(ExceptionAttribute.MESSAGE_BAD_REQUEST);
    }

}