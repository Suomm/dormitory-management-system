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

package xyz.tran4f.dms.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.attribute.RedisAttribute;
import xyz.tran4f.dms.exception.*;
import xyz.tran4f.dms.pojo.Captcha;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;
import xyz.tran4f.dms.utils.CaptchaUtils;
import xyz.tran4f.dms.utils.MD5Utils;
import xyz.tran4f.dms.utils.ServletUtils;
import xyz.tran4f.dms.validation.constraints.Id;
import xyz.tran4f.dms.validation.constraints.Password;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.*;
import static xyz.tran4f.dms.attribute.RedisAttribute.PREFIX_RESET_PASSWORD_KEY;
import static xyz.tran4f.dms.attribute.RedisAttribute.PREFIX_USER_CAPTCHA;

/**
 * <p>
 * 用户模块的控制器。主要功能为注册以及找回密码。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Validated
@Controller
@RequestMapping("/user")
@Api(tags = "用户模块的程序接口")
public class UserController extends BaseController<UserService> {

    /**
     * <p>
     * 找回密码时，生成数字签名所需要的结束符。
     * </p>
     */
    private static final String DELIMITER = "$";

    /**
     * <p>
     * 处理用户注册请求，将新用户信息写入数据库。
     * </p>
     *
     * @param user 通过表单封装的对象
     * @param emailCode 邮箱验证码
     * @param validateCode 邀请码
     */
    @ResponseBody
    @PostMapping("register")
    @ApiResponses(@ApiResponse(code = 200, message = "请求响应成功跳转到登陆界面"))
    @ApiOperation(value = "注册新用户", notes = "处理用户注册请求，将新用户信息写入数据库，并重定向到登陆界面。")
    public void register(@ApiParam(value = "用户信息", required = true) @Validated User user,
                         @ApiParam(value = "邮箱验证码", required = true) @NotBlank String emailCode,
                         @ApiParam(value = "邀请码", required = true) @NotBlank String validateCode) {
        // 获取缓存中的验证码对象
        Captcha captcha = redisUtils.get(PREFIX_USER_CAPTCHA + user.getId());
        // 验证码为空，用户还未发送验证码
        if (captcha == null) {
            throw new CaptchaException(USER_CAPTCHA_MISSING);
        }
        // 校验邮箱验证码
        CaptchaUtils.checkCaptcha(captcha, Captcha.defaultCaptcha(emailCode));
        // TODO 比对部门的邀请码是否正确
        log.info("校验用户输入的邀请码：{}", validateCode);
        // 调用 Service 层的方法注册用户，写入数据库
        service.register(user);
        // 注册成功之后删除验证码
        redisUtils.remove(user.getId());
        // 日志记录注册信息
        log.trace("注册学号为 {} 的用户成功", user.getId());
    }

    /**
     * <p>
     * 忘记密码的操作，由用户输入学号后，后台发送验证链接到邮箱，并将数据写到数据库。
     * </p>
     */
    @PostMapping("forget_password/{id}")
    @ApiOperation(value = "忘记密码操作", notes = "输入学号获取邮箱重置密码链接。")
    @ApiResponses(@ApiResponse(code = 200, message = "在当前页面加载返回的新页面"))
    public String forgetPassword(@PathVariable @ApiParam(value = "用户学号", required = true) @Id String id) {
        // 查询数据库中是否包含改用户
        User user = service.getById(id);
        // 查询不到要找回密码的用户
        if (user == null) {
            throw new UserNotFoundException();
        }
        // 生成唯一密匙（UUID）
        String secretKey = UUID.randomUUID().toString();
        // 设置过期时间：十分钟后过期
        long outDate = System.currentTimeMillis() + 10 * 60 * 1000;
        // 生成密匙键 ID$TIME$UUID
        String key = user.getId() + DELIMITER + outDate + DELIMITER + secretKey;
        // 生成数字签名，加密 KEY
        String digitalSignature = MD5Utils.encode(key);
        // 存入 Redis 缓存，设置十分钟后过期
        redisUtils.set(PREFIX_RESET_PASSWORD_KEY + user.getId(), new Captcha(secretKey, outDate), 10, TimeUnit.MINUTES);
        // 拼接请求路径
        String resetPassHref = ServletUtils.getBasePath() + "/user/reset_password/" + user.getId() + "/" + digitalSignature;
        // 要发送的邮件内容，默认一 HTML 的格式发送
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href=" + resetPassHref +
                " target='_BLANK'>点击我重新设置密码</a>" +
                "<br/>tips:本邮件超过10分钟,链接将会失效，需要重新申请'找回密码'";
        // 发送找回密码的邮件
        sendEmail("找回密码", emailContent, user.getEmail());
        // 转到修改密码成功界面
        return "user/success";
    }

    /**
     * <p>
     * 解析发送到用户邮箱的找回密码链接，返回重置密码的界面。
     * </p>
     */
    @GetMapping("reset_password/{id}/{sid}")
    @ApiResponses(@ApiResponse(code = 200, message = "在当前页面加载返回的新页面"))
    @ApiOperation(value = "重置密码检查", notes = "检查重置密码的密匙是否正确，正确则进入重置密码界面。")
    public String checkParam(@ApiParam(value = "密匙", required = true) @PathVariable String id,
                             @ApiParam(value = "学号", required = true) @PathVariable String sid) {
        // 获取之前存入缓存的验证码
        Captcha captcha = redisUtils.get(PREFIX_RESET_PASSWORD_KEY + id);
        // 无法找到匹配的用户
        redirectIf(captcha == null, USER_NOT_FOUND);
        // 连接过期了
        redirectIf(captcha.getOutDate() <= System.currentTimeMillis(), USER_RESET_PASSWORD_EXPIRE);
        // 生成密匙进行比对
        String digitalSignature = id + DELIMITER + captcha.getOutDate() + DELIMITER + captcha.getCode();
        // 密匙不完整
        redirectIf(!MD5Utils.matches(digitalSignature, sid), USER_RESET_PASSWORD_INCOMPLETE);
        // 将用户 ID 放入 Redis 缓存中
        redisUtils.set(RedisAttribute.PREFIX_RESET_PASSWORD_ID + id, id);
        // 解析成功转到重置密码界面
        return "user/reset_password";
    }

    // 满足条件就重定向到 /user/forget_password.html 页面
    private static void redirectIf(boolean test, String message) {
        if (test) {
            throw new RedirectException("/user/forget_password.html", message);
        }
    }

    /**
     * <p>
     * 重置密码操作，将新密码写入数据库，自动重定向到登陆界面。
     * </p>
     */
    @PostMapping("reset_password/{id}")
    @ApiResponses(@ApiResponse(code = 200, message = "请求响应成功跳转到登陆界面"))
    @ApiOperation(value = "重置密码请求", notes = "重置密码操作，将新密码写入数据库，自动重定向到登陆界面")
    public void resetPassword(@ApiParam(value = "学号", required = true) @PathVariable @Id String id,
                              @ApiParam(value = "密码", required = true) @Password String password) {
        // 从缓存中获取需要重置密码的用户学号
        String uid = redisUtils.get(RedisAttribute.PREFIX_RESET_PASSWORD_ID + id);
        // 获取不到用户学号，表明链接已经过期
        if (uid == null) {
            throw new InvalidOrOverdueException(USER_RESET_PASSWORD_OVERDUE);
        }
        // 数据写入数据库失败
        if (!service.resetPassword(new User(uid), password)) {
            throw new DatabaseException(USER_RESET_PASSWORD_FAIL_UPDATE);
        }
        // 日志记录修改密码成功
        log.trace("学号为 {} 的用户修改密码成功", id);
    }

    /**
     * <p>
     * 获取邮箱验证码，将数据保存到 Redis 缓存中，并发送邮箱通知用户。
     * </p>
     */
    @ResponseBody
    @PostMapping("get_captcha")
    @ApiOperation(value = "获取邮箱验证码", notes = "发送注册验证码到邮箱")
    @ApiResponses(@ApiResponse(code = 200, message = "提示用户验证码已经发送成功"))
    public void getCaptcha(@ApiParam(value = "学号", required = true) @Id String id,
                           @ApiParam(value = "邮箱", required = true) @NotBlank @Email String email) {
        // 生成六位、十分钟后过期的验证码
        Captcha code = CaptchaUtils.getCaptcha(6, 10 * 60 * 1000);
        // 编辑要发送的邮件内容
        String emailContent = "您的验证码为" + code.getCode() + "\t有效期为十分钟";
        // 调用 Service 发送邮件
        sendEmail("验证码", emailContent, email);
        // 将验证码内容存入 Redis 缓存中，并设置十分钟后过期
        redisUtils.set(PREFIX_USER_CAPTCHA + id, code, 10, TimeUnit.MINUTES);
    }

}
