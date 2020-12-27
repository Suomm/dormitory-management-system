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
     * 发送邮件到指定用户邮箱。
     * </p>
     *
     * @param email 用户邮箱地址
     * @param content 邮件内容
     */
    void sendEmail(String email, String subject, String content);

    /**
     * <p>
     * 重置密码的操作，加密密码存入数据库。
     * </p>
     *
     * @param user 封装ID的对象
     * @param password 重置后的密码
     * @return {@code true} 重置密码成功，{@code false} 重置密码失败
     */
    boolean resetPassword(User user, String password);

}
