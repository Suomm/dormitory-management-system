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

import org.jetbrains.annotations.NotNull;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户认证过期之后的操作。
 *
 * @author 王帅
 * @since 1.0
 */
public class AuthenticationExpiredHandler implements SessionInformationExpiredStrategy {

    // ===== 需要重复使用的常量 =====

    private static final String XML_HTTP_REQUEST = "XMLHttpRequest";
    private static final String X_REQUESTED_WITH = "X-Requested-With";

    // ===== 处理业务逻辑的方法 =====

    @Override
    public void onExpiredSessionDetected(@NotNull SessionInformationExpiredEvent event) throws IOException {
        HttpServletResponse response = event.getResponse();
        if (XML_HTTP_REQUEST.equals(event.getRequest().getHeader(X_REQUESTED_WITH))) {
            // 发送 401 状态码和详细信息
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("当前用户已在另一台设备上登录");
        } else {
            response.sendRedirect("/login.html");
        }
    }

}
