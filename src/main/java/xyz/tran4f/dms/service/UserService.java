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

package xyz.tran4f.dms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import xyz.tran4f.dms.exception.RegisterException;
import xyz.tran4f.dms.pojo.User;

/**
 * <p>
 * 用户操作的服务类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public interface UserService extends IService<User> {

    /**
     * <p>
     * 将用户信息写入数据库。
     * </p>
     *
     * @param user 封装好的数据对象
     * @exception RegisterException 注册失败抛出此异常
     */
    void register(User user) throws RegisterException;

    /**
     * <p>
     * 忘记密码之后重置密码的操作，加密密码存入数据库。
     * </p>
     *
     * @param id 学号
     * @param password 重置后的密码
     * @return {@code true} 重置密码成功，{@code false} 重置密码失败
     */
    boolean resetPassword(String id, String password);

    /**
     * <p>
     * 实现用户登录成功之后更改密码的操作。
     * </p>
     *
     * @param id 学号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return {@code true} 更改密码成功，{@code false} 更改密码失败
     */
    boolean changePassword(String id, String oldPassword, String newPassword);

    /**
     * <p>
     * 实现用户更改邮箱地址的操作。
     * </p>
     *
     * @param id 学号
     * @param newEmail 新邮箱地址
     * @return {@code true} 更改邮箱成功，{@code false} 更改邮箱失败
     */
    boolean changeEmail(String id, String newEmail);

}
