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

package xyz.tran4f.dms.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.tran4f.dms.exception.MessageException;
import xyz.tran4f.dms.exception.RedirectException;
import xyz.tran4f.dms.utils.I18nUtils;

import javax.validation.ConstraintViolationException;
import java.io.IOException;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.MESSAGE_BAD_REQUEST;
import static xyz.tran4f.dms.attribute.WebAttribute.WEB_LAST_EXCEPTION;

/**
 * <p>
 * 全局异常处理器。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Spring 依赖注入 I18nUtils 用于获取国际化异常信息

    private final I18nUtils i18nUtils;

    public GlobalExceptionHandler(I18nUtils i18nUtils) {
        this.i18nUtils = i18nUtils;
    }

    /**
     * <p>
     * 消息需要回显的并且页面要进行重定向操作。
     * </p>
     */
    @ExceptionHandler(RedirectException.class)
    public String sendRedirect(RedirectAttributes attr, RedirectException e) {
        String message = getMessage(e);
        attr.addFlashAttribute(WEB_LAST_EXCEPTION, message);
        log.info("处理异常：{} 重定向路径：{}", e.getClass().getName(), e.getRedirectUrl());
        return "redirect:" + e.getRedirectUrl();
    }

    /**
     * <p>
     * 处理自定义异常中，将异常的详细信息回显到前端界面。返回 HTTP 202 状态码，
     * 表示请求因出现异常而未被处理，并包含响应文本 responseText 表示异常的详
     * 细消息。
     * </p>
     *
     * @see MessageException
     */
    @ResponseBody
    @ExceptionHandler(MessageException.class)
    public ResponseEntity<String> sendMessage(MessageException exception) {
        // 获取异常键值指定的国际化消息
        String message = getMessage(exception);
        log.info("处理异常：{} 产生原因：{}", exception.getClass().getName(), message);
        return ResponseEntity.accepted().body(message);
    }

    /**
     * <p>
     * 获取消息的国际化内容。
     * </p>
     *
     * @param exception 需要回显的详细信息的异常
     * @return 国际化消息
     */
    private String getMessage(MessageException exception) {
        try {
            // 默认去本地查找 i18n 国际化消息
            return i18nUtils.getMessage(exception.getMessage(), exception.getArgs());
        } catch (NoSuchMessageException e) {
            // 本地没有匹配的国际化消息使用原始的消息
            return exception.getMessage();
        }
    }

    /**
     * <p>
     * 通过某种方法绕过了前端的校验，处理校验失败异常。返回 HTTP 400 状态码，表示一个
     * 非法请求。
     * </p>
     */
    @ResponseBody
    @ExceptionHandler({
            BindException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<String> validExceptionHandler() {
        return ResponseEntity.badRequest().body(i18nUtils.getMessage(MESSAGE_BAD_REQUEST));
    }

    @ResponseBody
    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> ioExceptionHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("生成文件失败");
    }

}
