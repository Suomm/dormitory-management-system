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

package xyz.tran4f.dms.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.tran4f.dms.pojo.User;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 与 Servlet 相关的工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class ServletUtils {

    private ServletUtils() {
    }

    private static ServletRequestAttributes servletRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }

    /**
     * <p>
     * 用户登陆成功之后获取用户信息。具体参考以下代码：
     * <blockquote><pre>
     *      SecurityContextHolder.getContext().getAuthentication().getPrincipal();
     * </pre></blockquote>
     * </p>
     *
     * @return 用户信息
     */
    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * <p>
     * 获取浏览器地址栏的根目录。满足：{@code http://localhost:8080/dms} 样式。
     * 具体参考以下代码：
     * <blockquote><pre>
     *     request.getScheme() + "://" +
     *     request.getServerName() + ":" +
     *     request.getServerPort() +
     *     request.getContextPath()
     * </pre></blockquote>
     * </p>
     *
     * @return 浏览器地址栏根目录
     */
    public static String getBasePath() {
        HttpServletRequest request = servletRequestAttributes().getRequest();
        StringBuilder builder = new StringBuilder();
        builder.append(request.getScheme())
                .append("://")
                .append(request.getServerName());
        int port = request.getServerPort();
        if (port != 80) {
            builder.append(":").append(port);
        }
        builder.append(request.getContextPath());
        return builder.toString();
    }

}
