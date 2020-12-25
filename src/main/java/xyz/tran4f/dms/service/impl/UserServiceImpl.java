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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

/**
 * @author 王帅
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

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
            throw new RegisterException(ExceptionAttribute.USER_REGISTER_FAIL_INSERT);
        }
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public String digitalSignature(User user) {
        String secretKey = UUID.randomUUID().toString(); //密钥
        Timestamp outDate = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000);//30分钟后过期
        user.setValidateCode(secretKey);
        user.setRegisterDate(outDate);
        // 该用户不在数据库中，即：该用户没有注册
        if (baseMapper.updateById(user) != 1) {
            throw new UserNotFoundException();
        }
        // 更新完之后查询出来，用于更新缓存
        User u = baseMapper.selectById(user.getId());
        user.setEmail(u.getEmail());
        String key = user.getId() + "$" + outDate.getTime() / 1000 * 1000 + "$" + secretKey;
        return passwordEncoder.encode(key);
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
        } catch (MessagingException e) {
            throw new EmailSendException("", e);
        }
//        javaMailSender.send(message);
        System.out.println(email);
        System.err.println(content);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkUser(String sid, String id) throws CheckFailedException {
        if ("".equals(sid) || "".equals(id)) {
            throw new CheckFailedException("链接不完整，请重新生成");
        }
        User user = baseMapper.selectById(id);
        if (user == null) {
            throw new CheckFailedException("链接错误，无法找到匹配用户，请重新申请找回密码。");
        }
        Timestamp outDate = user.getRegisterDate();
        if (outDate.getTime() <= System.currentTimeMillis()) {
            throw new CheckFailedException("链接已经过期，请重新申请找回密码.");
        }
        String digitalSignature = user.getId() + "$" + outDate.getTime() / 1000 * 1000 + "$" + user.getValidateCode();   //数字签名
        if (!passwordEncoder.matches(digitalSignature, sid)) {
            throw new CheckFailedException("链接不正确，是否已经过期了？重新申请吧");
        }
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
