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

    ResultInfo<User> register(User user);

    String digitalSignature(User user);

    void sendEmail(String id, String content);

    String checkUser(String sid, String id);

    boolean resetPassword(User user, String password);

}
