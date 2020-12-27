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

package xyz.tran4f.dms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * <p>
 * 解析页面跳转的控制器。所有方法都被标记为 {@link GetMapping} 已接受 GET 请求的操作，
 * 如有同名的 POST 请求，操作失败后解析页面不会改变地址栏 URL 地址，就是说当用户在浏览器
 * 地址栏回车后，有同名的 GET/POST 请求可以有效避免 405 错误状态码，增强用户体验。
 * </p>
 * <p>
 * 注解 {@link GetMapping} 的路径参数一定要以 {@code /} 开头，表示绝对路径；返回值会
 * 被视图解析器解析，一定不能以 {@code /} 开头。不然打成 JAR 文件后运行，请求路径后会报
 * {@link org.thymeleaf.exceptions.TemplateInputException} 异常！
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Controller
public class IndexController {

    @GetMapping({"/", "/index.html"})
    public String index() {
        return "index";
    }

    @GetMapping("/user/login.html")
    public String login() {
        return "user/login";
    }

    @GetMapping("/user/register.html")
    public String register() {
        return "user/register";
    }

    @GetMapping("/user/success.html")
    public String success() {
        return "user/success";
    }

    @GetMapping("/user/forget_password.html")
    public String forgetPassword() {
        return "user/forget_password";
    }

    @GetMapping("/manager/dashboard.html")
    public String dashboard() {
        return "manager/dashboard";
    }

}
