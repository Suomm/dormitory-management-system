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

package xyz.tran4f.dms.repository;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import xyz.tran4f.dms.mapper.UserMapper;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.utils.I18nUtils;

/**
 * <p>
 * Spring Security 框架 UserDetailsManager 接口的实现类，实现有关用户的相关操作。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Service("userManager")
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final I18nUtils  i18nUtils;
    private final UserMapper userMapper;

    public UserDetailsManagerImpl(I18nUtils i18nUtils, UserMapper userMapper) {
        this.i18nUtils  = i18nUtils;
        this.userMapper = userMapper;
    }

    /**
     * <p>
     * 从数据库中查找对象。
     * </p>
     *
     * @param username 用户名（学号）
     * @return 带有用户信息的{@code UserDetails}对象
     * @throws UsernameNotFoundException 如果学号不存在抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.selectById(username);
        if (user == null) {
            throw new UsernameNotFoundException(i18nUtils.getMessage("UserDetailsManagerImpl.userNotFound"));
        }
        // 设置用户名即为学号
        user.setUsername(user.getId());
        return user.setAuthorities(AuthorityUtils.createAuthorityList(user.getRole()));
    }

    @Override
    public void createUser(UserDetails user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateUser(UserDetails user) {
        User u = new User();
        u.setId(user.getUsername());
        u.setAccountNonLocked(user.isAccountNonLocked());
        userMapper.updateById(u);
    }

    @Override
    public void deleteUser(String username) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean userExists(String username) {
        return userMapper.selectById(username) != null;
    }

}
