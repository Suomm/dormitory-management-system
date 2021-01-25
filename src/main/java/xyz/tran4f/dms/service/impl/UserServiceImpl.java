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

package xyz.tran4f.dms.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.tran4f.dms.attribute.ExceptionAttribute;
import xyz.tran4f.dms.exception.DatabaseException;
import xyz.tran4f.dms.exception.InvalidPasswordException;
import xyz.tran4f.dms.exception.RegisterException;
import xyz.tran4f.dms.mapper.UserMapper;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;

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

    // 所需依赖的注入

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
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
        // 设置年级信息
        String grade = user.getId().substring(0, 2);
        user.setGrade("20" + grade);
        // 进行密码加密设置
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 插入用户信息失败
        if (baseMapper.insert(user) != 1) {
            throw new DatabaseException();
        }
    }

    private boolean update(User user) {
        return baseMapper.updateById(user) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resetPassword(String id, String password) {
        return update(new User(id).setPassword(passwordEncoder.encode(password)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        User user = baseMapper.selectById(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidPasswordException();
        }
        return update(user.setPassword(passwordEncoder.encode(newPassword)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeEmail(String id, String newEmail) {
        return update(User.builder().id(id).email(newEmail).build());
    }

}
