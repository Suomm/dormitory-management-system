/*
 * Copyright (C) 2020-2022 the original author or authors.
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

package cn.edu.tjnu.hxxy.dms.service.impl;

import cn.edu.tjnu.hxxy.dms.exception.BadCredentialException;
import cn.edu.tjnu.hxxy.dms.exception.RegisterException;
import cn.edu.tjnu.hxxy.dms.entity.User;
import cn.edu.tjnu.hxxy.dms.service.UserService;
import cn.edu.tjnu.hxxy.dms.util.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import cn.edu.tjnu.hxxy.dms.mapper.UserMapper;

/**
 * 用户相关操作的服务接口实现。
 *
 * @author 王帅
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    // 所需依赖的注入

    private final PasswordEncoder passwordEncoder;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean register(User user) throws RegisterException {
        // 检查数据库中是否有该用户
        if (baseMapper.selectById(user.getId()) != null) {
            throw new RegisterException("UserServiceImpl.existed");
        }
        // 设置年级信息
        user.setGrade(DateUtil.subYear() + user.getId().substring(0, 2) + "级");
        // 进行密码加密设置
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 插入用户信息
        return baseMapper.insert(user) == 1;
    }

    private boolean update(User user) {
        return baseMapper.updateById(user) == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changePassword(String id, @Nullable String oldPassword, String newPassword) {
        if (oldPassword == null) {
            return update(User.builder().id(id).password(passwordEncoder.encode(newPassword)).build());
        }
        User user = findUser(id);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialException("UserServiceImpl.incorrectPassword");
        }
        return update(user.setPassword(passwordEncoder.encode(newPassword)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeEmail(String id, @NotNull String oldEmail, String newEmail) {
        // 获取数据库中的用户
        User user = findUser(id);
        // 原邮箱地址有误
        if (!oldEmail.equals(user.getEmail())) {
            throw new BadCredentialException("UserServiceImpl.incorrectEmail");
        }
        return update(User.builder().id(id).email(newEmail).build());
    }

}
