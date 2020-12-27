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

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import xyz.tran4f.dms.attribute.WebAttribute;
import xyz.tran4f.dms.exception.RedirectException;
import xyz.tran4f.dms.exception.RuntimeMessageException;
import xyz.tran4f.dms.utils.I18nUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    @ExceptionHandler(RedirectException.class)
    public String sendRedirect(HttpSession session, RedirectException e) {
        session.setAttribute(WebAttribute.WEB_LAST_EXCEPTION, getMessage(e));
        return "redirect:" + e.getRedirectUrl();
    }

    /**
     * <p>
     * 处理自定义异常中，将异常的详细信息回显到前端界面。
     * </p>
     *
     * @see RuntimeMessageException
     * @see WebAttribute#WEB_LAST_EXCEPTION
     */
    @ExceptionHandler(RuntimeMessageException.class)
    public String sendMessage(HttpServletRequest request, RuntimeMessageException source) {
        String message = getMessage(source);
        // 将消息放入 request 域中用于回显
        request.getSession().setAttribute(WebAttribute.WEB_LAST_EXCEPTION, message);
        // 解析请求路径作为视图解析器返回
        String result = request.getServletPath();
        // 由于 GET/POST 请求都指向同一个 HTML 地址，所以要对路径做裁剪
        result = result.substring(1, result.length() - 5);
        log.info("处理异常 {} 解析路径 {}", source.getClass().getName(), result);
        return result;
    }

    private String getMessage(RuntimeMessageException source) {
        try {
            // 默认去本地查找 i18n 国际化消息
            return i18nUtils.getMessage(source.getMessage(), source.getArgs());
        } catch (NoSuchMessageException exception) {
            // 本地没有匹配的国际化消息使用原始的消息
            return source.getMessage();
        }
    }

}
