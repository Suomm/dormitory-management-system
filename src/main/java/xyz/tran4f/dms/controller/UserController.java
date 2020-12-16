/*
 * Copyright 2020 Wang Shuai
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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author 王帅
 * @since 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("login.html")
    public String toLogin() {
        return "user/login";
    }

    @GetMapping("register.html")
    public String toRegister() {
        return "user/register";
    }

    @PostMapping("/register")
    public String register(User user, HttpSession session) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.getBaseMapper().insert(user);
        session.setAttribute("user", user);
        return "redirect:/user/login.html";
    }

    /* ================================================================================================ */

    @RequestMapping("/upload")
    public String upload(MultipartFile file) throws IOException {
        long startTime = System.currentTimeMillis();
        System.out.println("fileName：" + file.getOriginalFilename());
        String path = "D:/" + new Date().getTime() + file.getOriginalFilename();
        File newFile = new File(path);
        file.transferTo(newFile);
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间：" + (endTime - startTime) + "ms");
        return "index";
    }

    @RequestMapping("/testAjax")
    @ResponseBody
    public User testAjax(User user) {
        System.out.println(user);
        user.setUsername("王五");
        return user;
    }

}
