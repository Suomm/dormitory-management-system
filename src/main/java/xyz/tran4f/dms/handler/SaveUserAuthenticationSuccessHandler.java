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

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import xyz.tran4f.dms.attribute.WebAttribute;
import xyz.tran4f.dms.pojo.SecurityUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 授权成功后将用户信息保存到 Session 域中。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class SaveUserAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // 获取 SecurityUser 对象，里面有封装好的 User 对象
        SecurityUser principal = (SecurityUser) authentication.getPrincipal();
        // 将 User 对象存入 Session 域中，命名为 user
        request.getSession().setAttribute(WebAttribute.WEB_SESSION_USER, principal.getUser());
        // 调用父类的方法实现页面跳转
        super.onAuthenticationSuccess(request, response, authentication);
    }

}
