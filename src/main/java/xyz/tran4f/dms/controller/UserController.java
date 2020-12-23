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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.tran4f.dms.pojo.ResultInfo;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.pojo.VerificationCode;
import xyz.tran4f.dms.service.UserService;
import xyz.tran4f.dms.utils.VerificationCodeUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author 王帅
 * @since 1.0
 */
@Controller
@EnableAsync
@RequestMapping("/user")
@SessionAttributes({"user", "code"})
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("login.html")
    public String toLogin() {
        return "user/login";
    }

    @GetMapping("register.html")
    public String toRegister() {
        return "user/register";
    }

    @PostMapping("register.html")
    public String register(@Validated User user, String emailCode,
                           BindingResult result, Model model,
                           @SessionAttribute VerificationCode code) {
        model.addAttribute("user", user);
        if (result.hasErrors()) {
            FieldError fieldError = result.getFieldError();
            assert fieldError != null;
            String message = fieldError.getDefaultMessage();
            model.addAttribute("error", message);
            return "user/register";
        }
        if (code.getOutDate() <= System.currentTimeMillis()) {
            model.addAttribute("error", "验证码过期了");
            return "user/register";
        }
        if (!code.getCode().equals(emailCode)) {
            model.addAttribute("error", "验证码不对");
            return "user/register";
        }
        if (!"123456".equals(user.getValidateCode())) {
            model.addAttribute("error", "邀请码不对哦");
            return "user/register";
        }
        ResultInfo<User> info = userService.register(user);
        if (!info.isComplete()) {
            model.addAttribute("error", info.getMessage());
            return "user/register";
        }
        return "redirect:/user/login.html";
    }

    @PostMapping("forget_password.html")
    public String validateCode(User user, Model model, HttpServletRequest request) {
        String digitalSignature = userService.digitalSignature(user);
        if (digitalSignature == null) {
            model.addAttribute("error", "该用户没有注册");
            return "user/forget_password";
        }
        String path = request.getContextPath();
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
        String resetPassHref = basePath+"user/reset_password?sid=" + digitalSignature+"&id="+user.getId();
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href="+resetPassHref +" target='_BLANK'>点击我重新设置密码</a>" +
                "<br/>tips:本邮件超过10分钟,链接将会失效，需要重新申请'找回密码'\t"+digitalSignature;
        userService.sendEmail(user.getId(), emailContent);
        model.addAttribute("emailContent", emailContent);
        return "user/success";
    }

    @GetMapping("forget_password.html")
    public String toForgetPassword() {
        return "user/forget_password";
    }

    @GetMapping("reset_password")
    public String checkResetPassword(String sid, String id, Model model) {
        String s = userService.checkUser(sid, id);
        if (s != null) {
            model.addAttribute("error", s);
            return "redirect:/user/forget_password";
        }
        User user = new User();
        user.setId(id);
        model.addAttribute("user", user);
        return "user/reset_password";
    }

    @PostMapping("reset_password")
    public String resetPassword(String password, Model model) {
        if (!userService.resetPassword((User) model.getAttribute("user"), password)) {
            model.addAttribute("error", "重置密码失败");
            return "user/reset_password";
        }
        return "redirect:/user/login.html";
    }

    @ResponseBody
    @PostMapping("pushVerificationCode")
    public void pushVerificationCode(String id, Model model) {
        VerificationCode code = VerificationCodeUtils.getVerificationCode(6, 10 * 60 * 1000);
        String emailContent = "您的验证码为" + code.getCode() + "\t有效期为十分钟";
        userService.sendEmail(id, emailContent);
        model.addAttribute("code", code);
    }

}
