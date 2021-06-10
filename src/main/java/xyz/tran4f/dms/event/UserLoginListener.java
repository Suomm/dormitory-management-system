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

package xyz.tran4f.dms.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import xyz.tran4f.dms.model.User;
import xyz.tran4f.dms.util.RedisUtils;

import java.util.concurrent.TimeUnit;

import static xyz.tran4f.dms.constant.RabbitConsts.*;
import static xyz.tran4f.dms.constant.RedisConsts.PREFIX_USER_LOCKED;

/**
 * 用户登陆的监听。
 *
 * @author 王帅
 * @since 1.0
 */
@Slf4j
@Component
public class UserLoginListener {

    private static final int MAX_LOGIN_COUNT = 10;

    // ===== 通过构造器注入依赖 =====

    private final RedisUtils redisUtils;
    private final RabbitTemplate rabbitTemplate;
    private final UserDetailsManager userDetailsManager;

    public UserLoginListener(RedisUtils redisUtils,
                             RabbitTemplate rabbitTemplate,
                             @Qualifier("userManager") UserDetailsManager userDetailsManager) {
        this.redisUtils = redisUtils;
        this.rabbitTemplate = rabbitTemplate;
        this.userDetailsManager = userDetailsManager;
    }

    /**
     * 用户名密码错误的事件监听。
     */
    @EventListener
    public void badCredentials(AuthenticationFailureBadCredentialsEvent event) {
        // 获取登陆的学号
        String username = event.getAuthentication().getPrincipal().toString();
        // 数据库中不存在该用户，不必记录登录次数
        if (!userDetailsManager.userExists(username)) {
            return;
        }
        // 根据键值获取对应的登录次数
        int limit = redisUtils.get(PREFIX_USER_LOCKED.concat(username), 1);
        if (limit < MAX_LOGIN_COUNT) {
            // 登录次数小于极限，每次登陆失败都要记录，记录三十分钟之后失效
            redisUtils.set(PREFIX_USER_LOCKED.concat(username), limit + 1, 30, TimeUnit.MINUTES);
        } else if (limit == MAX_LOGIN_COUNT){
            // 登录次数等于极限值时锁定用户
            setUserNonLocked(username, false);
            // 取消缓存失效时间，手动删除缓存，为了时间一致性
            redisUtils.set(PREFIX_USER_LOCKED.concat(username), limit + 1);
            // 将用户锁定状态发送到消息队列，等待解锁用户
            rabbitTemplate.convertAndSend(EXCHANGE_USER_DIRECT, QUEUE_USER_LOCKED_DELAY, username);
        }
        log.warn("用户 {} 登陆失败 {} 次", username, limit);
    }

    /**
     * 登陆成功监听。
     */
    @EventListener
    public void success(AuthenticationSuccessEvent event) {
        // 获取操作的用户名
        String username = event.getAuthentication().getPrincipal().toString();
        // 判断是否含有登陆失败的记录键，有的话删除对应的键
        if (redisUtils.hasKey(PREFIX_USER_LOCKED.concat(username))) {
            boolean remove = redisUtils.delete(PREFIX_USER_LOCKED.concat(username));
            log.info("用户 {} 登陆成功，移除键 {}：{}", username, username, remove);
        }
    }

    /**
     * 设置用户状态是否锁定。
     *
     * @param username 用户名（学号）
     * @param nonLock  {@code false} 锁定用户，{@code true} 解锁用户
     */
    private void setUserNonLocked(String username, boolean nonLock) {
        User user = (User) userDetailsManager.loadUserByUsername(username);
        user.setAccountNonLocked(nonLock);
        userDetailsManager.updateUser(user);
    }

    /**
     * 监听队列消息，实现解锁用户。
     *
     * @param username 用户名（学号）
     */
    @RabbitListener(queues = { QUEUE_USER_LOCKED_PROCESS })
    public void unlocked(String username) {
        redisUtils.delete(PREFIX_USER_LOCKED.concat(username));
        setUserNonLocked(username, true);
        log.info("取消锁定用户 {}", username);
    }

}
