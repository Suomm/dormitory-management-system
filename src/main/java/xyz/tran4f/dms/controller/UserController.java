package xyz.tran4f.dms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.tran4f.dms.domain.User;
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
@SessionAttributes("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("/login")
    public String login(String username, String password, HttpSession session) {
        System.out.println(username);
        User user = userService.login(username, password);
        session.setAttribute("user", user);
        return "redirect:/welcome.html";
    }

    @RequestMapping("/register")
    public String register(User user, HttpSession session) {
        System.out.println(user);
        return "redirect:/welcome.html";
    }

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
