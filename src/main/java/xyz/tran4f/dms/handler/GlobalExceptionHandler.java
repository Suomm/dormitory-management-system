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

package xyz.tran4f.dms.handler;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 全局异常处理器。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * <p>
     * 处理 {@link HttpRequestMethodNotSupportedException} 异常，将用户错误输入的 POST 请求
     * 转换为 GET 请求，避免出现 {@code 405} 错误页面。
     * </p>
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String toOrigin(HttpServletRequest request) {
        return "redirect:" + request.getServletPath() + ".html";
    }

}
