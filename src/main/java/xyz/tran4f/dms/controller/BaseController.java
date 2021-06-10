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

package xyz.tran4f.dms.controller;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import xyz.tran4f.dms.constant.RabbitConsts;
import xyz.tran4f.dms.model.Email;
import xyz.tran4f.dms.util.RedisUtils;

/**
 * 所有业务控制器的父类。它是一个抽象类，只能被继承而不能被初始化，用于定义控制器的一些
 * 基本通用方法。
 *
 * @param <S> 服务层接口
 * @author 王帅
 * @since 1.0
 */
public abstract class BaseController<S extends IService<?>> {

    /**
     * 引用的服务层接口。
     */
    @Autowired
    protected S service;

    /**
     * Redis 缓存操作工具类。
     */
    @Autowired
    protected RedisUtils redisUtils;

    /**
     * Rabbit MQ 消息队列模板。
     */
    @Autowired
    protected RabbitTemplate rabbitTemplate;

    /**
     * 通过消息队列实现异步发送邮件功能。
     *
     * @param subject   主题
     * @param text      内容
     * @param to        收件人
     */
    protected final void sendEmail(String subject, String text, String to) {
        rabbitTemplate.convertAndSend(RabbitConsts.QUEUE_EMAIL, new Email(subject, text, to));
    }

}
