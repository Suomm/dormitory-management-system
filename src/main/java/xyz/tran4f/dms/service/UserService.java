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
import xyz.tran4f.dms.pojo.ResultInfo;
import xyz.tran4f.dms.pojo.User;

/**
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
     * @return 带有信息的结果集
     */
    ResultInfo<User> register(User user);

    /**
     * <p>
     * 获取对象的数字签名用于找回密码校验。
     * </p>
     *
     * @param user 封装有ID的对象
     * @return {@code null} 说明生成数字签名失败
     */
    String digitalSignature(User user);

    /**
     * <p>
     * 发送邮件到指定用户邮箱。
     * </p>
     *
     * @param email 用户邮箱地址
     * @param content 邮件内容
     */
    void sendEmail(String email, String content);

    /**
     * <p>
     * 找回密码时，过期时间与密匙的检查。
     * </p>
     *
     * @param sid 要检查的密匙
     * @param id 用户的学号
     * @return 检查的错误信息说明，检查通过则返回 {@code null}
     */
    String checkUser(String sid, String id);

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
