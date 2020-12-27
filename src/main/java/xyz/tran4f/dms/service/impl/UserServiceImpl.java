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

    private static final String DELIMITER = "$";

    // 所需依赖的注入：邮件发送，密码加密

    private final JavaMailSender  javaMailSender;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(JavaMailSender javaMailSender, PasswordEncoder passwordEncoder) {
        this.javaMailSender  = javaMailSender;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * <p>
     * 忽略时间的毫秒值，减少进出数据库的误差。
     * </p>
     */
    private long ignore(Timestamp stamp) {
        return stamp.getTime() / 1000 * 1000;
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
    @NotNull
    @Override
    public String digitalSignature(User user) {
        // 生成唯一密匙
        String secretKey = UUID.randomUUID().toString();
        // 设置过期时间：十分钟后过期
        Timestamp outDate = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000);
        // 保存在用户实体中
        user.setValidateCode(secretKey);
        user.setRegisterDate(outDate);
        // 该用户不在数据库中，即：该用户没有注册
        if (baseMapper.updateById(user) != 1) {
            throw new UserNotFoundException();
        }
        // 更新完之后查询出来，用于更新缓存
        User u = baseMapper.selectById(user.getId());
        user.setEmail(u.getEmail());
        // 生成密匙键 ID$TIME$UUID
        String key = user.getId() + DELIMITER + ignore(outDate) + DELIMITER + secretKey;
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
    public void checkUser(String sid, String id) throws CheckFailedException {
        // 没有生成的密匙
        if ("".equals(sid)) {
            throw new CheckFailedException(ExceptionAttribute.USER_CHECK_MISSING_SID);
        }
        User user = baseMapper.selectById(id);
        // 无法找到匹配的用户
        if (user == null) {
            throw new CheckFailedException(ExceptionAttribute.USER_NOT_FOUND);
        }
        Timestamp outDate = user.getRegisterDate();
        // 连接过期了
        if (outDate.getTime() <= System.currentTimeMillis()) {
            throw new CheckFailedException(ExceptionAttribute.USER_CHECK_EXPIRE);
        }
        String digitalSignature = user.getId() + DELIMITER + ignore(outDate) + DELIMITER + user.getValidateCode();   //数字签名
        // 密匙不完整
        if (!passwordEncoder.matches(digitalSignature, sid)) {
            throw new CheckFailedException(ExceptionAttribute.USER_CHECK_INCOMPLETE);
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
