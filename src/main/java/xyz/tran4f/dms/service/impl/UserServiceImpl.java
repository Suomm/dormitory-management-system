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

package xyz.tran4f.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.tran4f.dms.attribute.ExceptionAttribute;
import xyz.tran4f.dms.exception.*;
import xyz.tran4f.dms.mapper.UserMapper;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;

import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * <p>
 * 用户操作服务类的实现。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 所需依赖的注入：邮件发送，密码加密

    private final JavaMailSender  javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder) {
        this.javaMailSender  = javaMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(User user) {
        // 检查数据库中是否有该用户
        if (baseMapper.selectById(user.getId()) != null) {
            throw new RegisterException(ExceptionAttribute.USER_REGISTER_REPEAT);
        }
        // 进行密码加密设置
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 插入用户信息失败
        if (baseMapper.insert(user) != 1) {
            throw new DatabaseException(ExceptionAttribute.USER_REGISTER_FAIL_INSERT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void sendEmail(String email, String subject, String content) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom("suomm.macher@foxmail.com");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (Exception e) {
            throw new EmailSendException(ExceptionAttribute.USER_EMAIL_FAIL, e);
        }
//        javaMailSender.send(message);
        System.out.println(email);
        System.err.println(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return baseMapper.updateById(user) == 1;
    }

}
