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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.tran4f.dms.attribute.RedisAttribute;
import xyz.tran4f.dms.utils.CaptchaUtils;

/**
 * <p>
 * 在项目启动时，初始化 Redis 缓存中的内容，并每隔一段时间更新其中内容。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
@Component
public class RedisInitializingParams implements InitializingBean {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisInitializingParams(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        setCaptcha(); // 设置部门邀请码
    }

    /**
     * <p>
     * 定时任务：每晚十二点整更新缓存中的部门邀请码。
     * </p>
     */
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void setCaptcha() {
        redisTemplate.opsForValue().set(RedisAttribute.KEY_CAPTCHA, CaptchaUtils.getCode(6));
    }

}
