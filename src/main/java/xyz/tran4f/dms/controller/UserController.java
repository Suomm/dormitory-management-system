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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import xyz.tran4f.dms.exception.InvalidOrOverdueException;
import xyz.tran4f.dms.exception.MissingAttributeException;
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

import static xyz.tran4f.dms.attribute.RedisAttribute.*;

/**
 * <p>
 * 用户模块的具体业务流程控制。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/user")
@Api(tags = "用户模块的程序接口")
public class UserController extends BaseController<UserService> {

    /**
     * <p>
     * 找回密码时，生成数字签名所需要的分隔符。
     * </p>
     */
    private static final String DELIMITER = "$";

    // === 注册用户以及忘记密码之后重置密码请求操作 ===

    /**
     * <p>
     * 处理用户注册请求，将新用户信息写入数据库。
     * </p>
     *
     * @param user 通过表单封装的对象
     * @param emailCode 邮箱验证码
     * @param validateCode 邀请码
     * @return {@code true} 注册用户成功，{@code false} 注册用户失败
     */
    @PostMapping("register")
    @ApiOperation(value = "注册新用户", notes = "处理用户注册请求，将新用户信息写入数据库，并重定向到登陆界面。")
    public boolean register(@ApiParam(value = "用户信息", required = true) @Validated User user,
                            @ApiParam(value = "邮箱验证码", required = true) @NotBlank String emailCode,
                            @ApiParam(value = "部门邀请码", required = true) @NotBlank String validateCode) {
        String key = PREFIX_USER_CAPTCHA.concat(user.getId());
        // 获取缓存中的验证码对象
        Captcha captcha = redisUtils.get(key);
        // 校验邮箱验证码
        CaptchaUtils.checkCaptcha(captcha, Captcha.defaultCaptcha(emailCode));
        // 检验部门的邀请码是否正确
        if (!validateCode.equals(redisUtils.get(KEY_CAPTCHA))) {
            throw new InvalidOrOverdueException("UserController.noPermission");
        }
        // 注册用户并在其成功之后删除验证码
        if (service.register(user)) {
            redisUtils.delete(key);
            log.info("注册学号为 {} 的用户成功", user.getId());
            return true;
        }
        return false;
    }

    /**
     * <p>
     * 忘记密码的操作，由用户输入学号后，后台发送验证链接到邮箱。
     * </p>
     *
     * @param id 学号
     */
    @PostMapping("forget-password/{id}")
    @ApiOperation(value = "忘记密码操作", notes = "输入学号获取邮箱重置密码链接。")
    public void forgetPassword(@PathVariable @ApiParam(value = "用户学号", required = true) @Id String id) {
        // 查询数据库中是否包含改用户
        User user = service.findUser(id);
        // 生成唯一密匙（UUID）
        String secretKey = UUID.randomUUID().toString();
        // 设置过期时间：十分钟后过期
        long outDate = System.currentTimeMillis() + 10 * 60 * 1000;
        // 生成密匙键 ID$TIME$UUID
        String key = user.getId() + DELIMITER + outDate + DELIMITER + secretKey;
        // 生成数字签名，加密 KEY
        String digitalSignature = MD5Utils.encode(key).toUpperCase();
        // 存入 Redis 缓存，设置十分钟后过期
        redisUtils.set(PREFIX_RESET_PASSWORD.concat(user.getId()), new Captcha(secretKey, outDate), 10, TimeUnit.MINUTES);
        // 拼接请求路径
        String resetPassHref = ServletUtils.getBasePath() + "/reset-password.html" + "?id=" + user.getId() + "&sid=" + digitalSignature;
        // 要发送的邮件内容，默认一 HTML 的格式发送
        String emailContent = "请勿回复本邮件.点击下面的链接,重设密码<br/><a href=" + resetPassHref +
                " target='_BLANK'>点击我重新设置密码</a><br/>TIPS:本邮件超过10分钟,链接将会失效，需要重新申请'找回密码'";
        // 发送找回密码的邮件
        sendEmail("找回密码", emailContent, user.getEmail());
    }

    /**
     * <p>
     * 解析发送到用户邮箱的找回密码链接，返回重置密码的界面。
     * </p>
     *
     * @param id 学号
     * @param sid 密匙
     * @return 重置密码界面
     */
    @PostMapping("checkup")
    @ApiOperation(value = "重置密码检查", notes = "检查重置密码的密匙是否正确，正确则进入重置密码界面。")
    public boolean checkup(@ApiParam(value = "密匙", required = true) @NotBlank String id,
                           @ApiParam(value = "学号", required = true) @NotBlank String sid) {
        // 获取之前存入缓存的验证链接
        Captcha captcha = redisUtils.get(PREFIX_RESET_PASSWORD.concat(id));
        // 没有输入自己的学号以及链接过期了
        return captcha != null && captcha.getOutDate() > System.currentTimeMillis() &&
                MD5Utils.matches(id + DELIMITER + captcha.getOutDate() + DELIMITER + captcha.getCode(), sid);
    }

    /**
     * <p>
     * 重置密码操作，将新密码写入数据库，自动重定向到登陆界面。
     * </p>
     *
     * @param id 学号
     * @param password 重置后的密码
     * @return {@code true} 更改密码成功，{@code false} 更改密码失败
     */
    @PostMapping("reset-password/{id}")
    @ApiOperation(value = "重置密码请求", notes = "重置密码操作，将新密码写入数据库，自动重定向到登陆界面")
    public boolean resetPassword(@ApiParam(value = "学号", required = true) @PathVariable @Id String id,
                                 @ApiParam(value = "新密码", required = true) @Password String password) {
        String key = PREFIX_RESET_PASSWORD.concat(id);
        // 获取不到用户学号，表明链接已经过期
        if (!redisUtils.hasKey(key)) {
            throw new MissingAttributeException("UserController.badCredentials");
        }
        // 数据写入数据库失败
        if (service.changePassword(id, null, password)) {
            redisUtils.delete(key);
            log.info("用户 {} 找回密码成功", id);
            return true;
        }
        return false;
    }

    // === 获取邮箱验证码请求 ===

    /**
     * <p>
     * 获取邮箱验证码，将数据保存到 Redis 缓存中，并发送邮箱通知用户。
     * </p>
     *
     * @param id 学号
     * @param email 邮箱
     */
    @PostMapping("getCaptcha")
    @ApiOperation(value = "获取邮箱验证码", notes = "发送注册验证码到邮箱")
    public void getCaptcha(@ApiParam(value = "学号", required = true) @Id String id,
                           @ApiParam(value = "邮箱", required = true) @NotBlank @Email String email) {
        // 生成六位、十分钟后过期的验证码
        Captcha code = CaptchaUtils.getCaptcha(6, 10 * 60 * 1000);
        // 编辑要发送的邮件内容
        String emailContent = "您的验证码为" + code.getCode() + "\t有效期为十分钟";
        // 发送验证码邮件
        sendEmail("验证码", emailContent, email);
        // 将验证码内容存入 Redis 缓存中，并设置十分钟后过期
        redisUtils.set(PREFIX_USER_CAPTCHA.concat(id), code, 10, TimeUnit.MINUTES);
    }

    // === 用户登陆成功之后更改相关信息操作 ===

    @GetMapping("principal")
    public User principal() {
        return ServletUtils.getUser();
    }

    /**
     * <p>
     * 更改用户密码。
     * </p>
     *
     * @param id 学号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return {@code true} 更改密码成功，{@code false} 更改密码失败
     */
    @PutMapping("change-password/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "学号", required = true),
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, paramType = "query")
    })
    @ApiOperation(value = "更改密码", notes = "输入旧密码和新密码更改密码")
    public boolean changePassword(@PathVariable @Id String id,
                                  @Password String oldPassword,
                                  @Password String newPassword) {
        return service.changePassword(id, oldPassword, newPassword);
    }

    /**
     * <p>
     * 更改用户邮箱。
     * </p>
     *
     * @param id 学号
     * @param oldEmail 旧邮箱
     * @param newEmail 新邮箱
     * @param emailCode 邮箱验证码
     * @return {@code true} 更改邮箱成功，{@code false} 更改邮箱失败
     */
    @PutMapping("change-email/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "学号", required = true),
            @ApiImplicitParam(name = "oldEmail", value = "旧邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(name = "newEmail", value = "新邮箱", required = true, paramType = "query"),
            @ApiImplicitParam(name = "emailCode", value = "验证码", required = true, paramType = "query")
    })
    @ApiOperation(value = "更改邮箱", notes = "输入新邮箱地址和验证码更改邮箱")
    public boolean changeEmail(@PathVariable @Id String id,
                               @NotBlank @Email String oldEmail,
                               @NotBlank @Email String newEmail,
                               @NotBlank String emailCode) {
        // 获取缓存中的验证码对象
        Captcha captcha = redisUtils.get(PREFIX_USER_CAPTCHA.concat(id));
        // 校验邮箱验证码
        CaptchaUtils.checkCaptcha(captcha, Captcha.defaultCaptcha(emailCode));
        // 修改邮箱成功后删除验证码
        if (service.changeEmail(id, oldEmail, newEmail)) {
            redisUtils.delete(PREFIX_USER_CAPTCHA.concat(id));
            return true;
        }
        return false;
    }

}
