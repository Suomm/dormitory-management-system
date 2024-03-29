/*
 * Copyright (C) 2020-2022 the original author or authors.
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

package cn.edu.tjnu.hxxy.dms.event;

import cn.edu.tjnu.hxxy.dms.exception.AbstractMessageException;
import cn.edu.tjnu.hxxy.dms.util.I18nUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器。
 *
 * @author 王帅
 * @since 1.0
 */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // Spring 依赖注入 I18nUtils 用于获取国际化异常信息

    private final I18nUtil i18nUtil;

    /**
     * 处理自定义异常中，将异常的详细信息回显到前端界面。返回 HTTP 400 状态码，
     * 表示请求因出现异常而未被处理，并包含响应文本 responseText 表示异常的详
     * 细消息。
     *
     * @see AbstractMessageException
     */
    @ExceptionHandler(AbstractMessageException.class)
    public ResponseEntity<String> sendMessage(AbstractMessageException exception) {
        return ResponseEntity.badRequest().body(getMessage(exception));
    }

    /**
     * 获取消息的国际化内容。
     *
     * @param exception 需要回显的详细信息的异常
     * @return 国际化消息
     */
    private String getMessage(AbstractMessageException exception) {
        try {
            // 默认去本地查找 i18n 国际化消息
            return i18nUtil.getMessage(exception.getMessage(), exception.getArgs());
        } catch (NoSuchMessageException e) {
            // 本地没有匹配的国际化消息使用原始的消息
            return exception.getMessage();
        }
    }

    /**
     * 通过某种方法绕过了前端的校验，处理校验失败异常。返回 HTTP 400 状态码，表示一个
     * 非法请求。
     */
    @ExceptionHandler({
            BindException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<String> validExceptionHandler() {
        return ResponseEntity.badRequest()
                .body(i18nUtil.getMessage("GlobalExceptionHandler.incorrectRequest"));
    }

}
