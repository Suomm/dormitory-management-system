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

package xyz.tran4f.dms.controller;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import xyz.tran4f.dms.attribute.WebAttribute;
import xyz.tran4f.dms.utils.RedisUtil;

/**
 * <p>
 * 所有业务控制器的父类。它是一个抽象类，只能被继承而不能被初始化，用于定义控制器的一些
 * 基本通用方法。
 * </p>
 *
 * @param <S> 引用的服务层对象
 * @author 王帅
 * @since 1.0
 */
public abstract class BaseController<S> {

    /**
     * 当前控制器所引用的 Service 对象。
     */
    protected S service;

    protected RedisUtil redisUtil;

    protected BaseController(S service, RedisUtil redisUtil) {
        this.service = service;
        this.redisUtil = redisUtil;
    }

    /**
     * <p>
     * 用于移除 Session 域中保存的上次错误信息。
     * </p>
     */
    protected void removeErrorMessage() {
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        attributes.removeAttribute(WebAttribute.WEB_LAST_EXCEPTION, RequestAttributes.SCOPE_SESSION);
    }

}
