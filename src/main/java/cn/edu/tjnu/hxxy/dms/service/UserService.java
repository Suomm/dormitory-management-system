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

package cn.edu.tjnu.hxxy.dms.service;

import cn.edu.tjnu.hxxy.dms.exception.RegisterException;
import cn.edu.tjnu.hxxy.dms.exception.UserNotFoundException;
import cn.edu.tjnu.hxxy.dms.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * 用户相关操作的服务层接口。
 *
 * @author 王帅
 * @since 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 根据学号查找对应的学生信息，没有该学生的信息则抛出异常。
     *
     * @param id 学号
     * @return 学生信息
     * @throws UserNotFoundException 没有找到该学生的信息抛出异常
     */
    default User findUser(final String id) throws UserNotFoundException {
        return Optional.ofNullable(getById(id))
                .orElseThrow(() -> new UserNotFoundException("UserService.notFound", id));
    }

    /**
     * 注册用户操作，将封装好的用户信息保存到数据库。
     *
     * @param user 封装好的数据对象
     * @return {@code true} 注册用户成功，{@code false} 注册用户失败
     * @throws RegisterException 注册用户失败抛出此异常
     */
    boolean register(User user) throws RegisterException;

    /**
     * 实现用户更改密码的操作（<b>可以不提供旧密码</b>）。
     *
     * @param id          学号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return {@code true} 更改密码成功，{@code false} 更改密码失败
     */
    boolean changePassword(String id, @Nullable String oldPassword, String newPassword);

    /**
     * 实现用户更改邮箱地址的操作。
     *
     * @param id       学号
     * @param oldEmail 旧邮箱地址
     * @param newEmail 新邮箱地址
     * @return {@code true} 更改邮箱成功，{@code false} 更改邮箱失败
     */
    boolean changeEmail(String id, String oldEmail, String newEmail);

}
