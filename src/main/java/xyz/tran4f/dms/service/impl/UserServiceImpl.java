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
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.tran4f.dms.mapper.UserMapper;
import xyz.tran4f.dms.pojo.ResultInfo;
import xyz.tran4f.dms.pojo.User;
import xyz.tran4f.dms.service.UserService;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * @author 王帅
 * @since 1.0
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResultInfo<User> register(User user) {
        // 检查数据库中是否有该用户
        if (baseMapper.selectById(user.getId()) != null) {
            return new ResultInfo<>("该用户已被注册过");
        }
        // 密码加密和用户角色设置
        user.setPassword(passwordEncoder.encode(user.getPassword()));
//        user.setRole("ROLE_USER");
        // 插入用户信息失败
        if (baseMapper.insert(user) != 1) {
            return new ResultInfo<>("注册失败");
        }
        return new ResultInfo<>(user);
    }

    @Override
    public String digitalSignature(User user) {
        String secretKey = UUID.randomUUID().toString(); //密钥
        Timestamp outDate = new Timestamp(System.currentTimeMillis() + 10 * 60 * 1000);//30分钟后过期
        user.setValidateCode(secretKey);
        user.setRegisterDate(outDate);
        if (baseMapper.updateById(user) != 1) {
            return null;
        }
        String key = user.getId()+"$"+outDate.getTime()/1000*1000+"$"+secretKey;
        return passwordEncoder.encode(key);
    }

    @Async
    @Override
    public void sendEmail(String content) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println(content);
    }

    @Override
    public String checkUser(String sid, String id) {
        if ("".equals(sid) || "".equals(id)) {
            return "链接不完整，请重新生成";
        }
        User user = baseMapper.selectById(id);
        if (user == null) {
            return "链接错误，无法找到匹配用户，请重新申请找回密码。";
        }
        Timestamp outDate = user.getRegisterDate();
        if (outDate.getTime() <= System.currentTimeMillis()) {
            return "链接已经过期，请重新申请找回密码.";
        }
        String digitalSignature = user.getId() + "$" + outDate.getTime()/1000*1000 + "$" + user.getValidateCode();   //数字签名
        if (!passwordEncoder.matches(digitalSignature, sid)) {
            return "链接不正确，是否已经过期了？重新申请吧";
        }
        return null;
    }

    @Override
    public boolean resetPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return baseMapper.updateById(user) == 1;
    }

}
