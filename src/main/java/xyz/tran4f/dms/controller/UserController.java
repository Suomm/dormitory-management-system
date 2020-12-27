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

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.ExceptionAttribute;
import xyz.tran4f.dms.attribute.WebAttribute;
import xyz.tran4f.dms.exception.*;
import xyz.tran4f.dms.pojo.Captcha;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;
import xyz.tran4f.dms.utils.CaptchaUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户模块的控制器。主要功能为注册登录以及找回密码。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Controller
@EnableAsync // 实现异步发送邮件
@RequestMapping("/user")
@SessionAttributes({WebAttribute.WEB_SESSION_USER, WebAttribute.WEB_SESSION_CAPTCHA})
public class UserController extends BaseController<UserService> {

    // 通过构造器注入依赖
    public UserController(UserService userService) {
        super(userService);
    }

    // 请求处理方法

    /**
     * <p>
     * 处理用户注册请求。
     * </p>
     *
     * @param user 通过表单封装的对象，会经过校验
     * @param emailCode 邮箱验证码
     * @param result 校验错误信息封装
     * @param model 数据模型用于返回数据
     */
    @PostMapping("register.html")
    public String register(@Validated User user, String emailCode,
                           BindingResult result, Model model) {
        // 添加用户注册信息到Session域中用于数据回显
        model.addAttribute(WebAttribute.WEB_SESSION_USER, user);
        // 数据校验出现错误
        if (result.hasErrors()) {
            throw new IllegalRequestException();
        }
        Captcha captcha = (Captcha) model.getAttribute("captcha");
        // 获取不到 Session 域中的对象
        if (captcha == null) {
            throw new MissingAttributeException(ExceptionAttribute.USER_MISSING_CAPTCHA);
        }
        // 校验邮箱验证码
        CaptchaUtils.checkCaptcha(captcha, Captcha.defaultCaptcha(emailCode));
        // 比对部门的邀请码是否正确
        if (!"123456".equals(user.getValidateCode())) {
            model.addAttribute(WebAttribute.WEB_LAST_EXCEPTION, "邀请码不对哦");
            return "user/register";
        }
        // 调用 Service 层的方法注册用户，写入数据库
        service.register(user);
        // 删除 Session 域中的错误信息
        removeErrorMessage();
        // 注册成功，重定向到登陆页面进行登录
        return "redirect:/user/login.html";
    }

    /**
     * <p>
     * 忘记密码的操作，由用户输入学号后，后台发送验证链接到邮箱，并将数据写到数据库。
     * </p>
     */
    @PostMapping("forget_password.html")
    public String forgetPassword(User user, HttpServletRequest request) {
        // 通过 Service 层获取数字签名，并将UUID和过期时间写入数据库
        String digitalSignature = service.digitalSignature(user);
        // 通过 request 拼接而成的请求路径
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" +
                request.getServerPort() + request.getContextPath() + "/";
        String resetPassHref = basePath + "user/reset_password.html" + "?sid=" +
                digitalSignature + "&id=" + user.getId();
        // 要发送的邮件内容，默认一 HTML 的格式发送
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href=" + resetPassHref +
                " target='_BLANK'>点击我重新设置密码</a>" +
                "<br/>tips:本邮件超过10分钟,链接将会失效，需要重新申请'找回密码'\t" + digitalSignature;
        // 发送找回密码的邮件
        service.sendEmail(user.getEmail(), "找回密码", emailContent);
        // 用于在网页上展示邮件内容，可删除
        request.setAttribute("emailContent", emailContent);
        // 删除 Session 域中的错误信息
        removeErrorMessage();
        return "user/success";
    }

    /**
     * <p>
     * 解析发送到用户邮箱的找回密码链接，返回重置密码的界面。
     * </p>
     */
    @GetMapping("reset_password.html")
    public String checkSID(String sid, String id, Model model) {
        try {
            // 检查加密的数字密匙是否正确
            service.checkUser(sid, id);
        } catch (CheckFailedException e) {
            // 加密的密匙不正确，进行页面的重定向
            throw new RedirectException("/user/forget_password.html", e);
        }
        // 将用户 ID 放入 session 域中
        User user = new User(id);
        model.addAttribute(WebAttribute.WEB_SESSION_USER, user);
        // 删除 Session 域中的错误信息
        removeErrorMessage();
        return "user/reset_password";
    }

    /**
     * <p>
     * 重置密码的操作，更新数据库。
     * </p>
     */
    @PostMapping("reset_password.html")
    public String resetPassword(String password, Model model) {
        User user = (User) model.getAttribute(WebAttribute.WEB_SESSION_USER);
        // 获取不到 Session 域中的对象
        if (user == null) {
            throw new MissingAttributeException(ExceptionAttribute.MESSAGE_MISSING_ATTRIBUTE,
                    "Session", "user");
        }
        // 数据写入数据库失败
        if (!service.resetPassword(user, password)) {
            throw new DatabaseException(ExceptionAttribute.USER_RESET_PASSWORD_FAIL_UPDATE);
        }
        // 删除 Session 域中的错误信息
        removeErrorMessage();
        // 重置密码成功，自动重定向到登陆界面
        return "redirect:/user/login.html";
    }

    /**
     * <p>
     * 获取邮箱验证码，将数据保存到 session 域中，并发送邮箱通知用户。
     * </p>
     */
    @ResponseBody
    @PostMapping("getCaptcha")
    public void getCaptcha(String email, Model model) {
        // 生成六位、十分钟后过期的验证码
        Captcha code = CaptchaUtils.getCaptcha(6, 10 * 60 * 1000);
        // 编辑要发送的邮件内容
        String emailContent = "您的验证码为" + code.getCode() + "\t有效期为十分钟";
        // 调用 Service 发送邮件
        service.sendEmail(email, "验证码", emailContent);
        // 将验证码内容存入 Session 域中
        model.addAttribute(WebAttribute.WEB_SESSION_CAPTCHA, code);
    }

}
