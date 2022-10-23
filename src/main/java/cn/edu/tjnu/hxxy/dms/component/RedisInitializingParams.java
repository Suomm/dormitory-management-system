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

package cn.edu.tjnu.hxxy.dms.component;

import cn.edu.tjnu.hxxy.dms.util.CaptchaUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cn.edu.tjnu.hxxy.dms.constant.RedisConst;

/**
 * 在项目启动时，向 Redis 缓存中存入数据，并每隔一段时间更新其中内容。
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
     * 定时任务：每晚十二点整更新缓存中的部门邀请码。
     */
    @Scheduled(cron = "0 0 0 */1 * ?")
    public void setCaptcha() {
        redisTemplate.opsForValue().set(RedisConst.KEY_CAPTCHA, CaptchaUtil.getCode(6));
    }

}
