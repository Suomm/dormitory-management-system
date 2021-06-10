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

package xyz.tran4f.dms.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * 获取 i18n 国际化消息的工具类。使用时通过 Spring 依赖注入即可。
 * 消息内容详见：{@code resources/messages.properties} 国际化配置文件。
 *
 * @author 王帅
 * @since 1.0
 */
@Component
public class I18nUtils {

    // Spring MessageSource 依赖注入

    private final MessageSource messageSource;

    public I18nUtils(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 根据所给键的值获取 {@code i18n/messages} 国际化配置文件所对应的消息。其中，区域信息通过
     * {@link LocaleContextHolder#getLocale()} 方法获得。
     *
     * @param key 国际化消息的键值
     * @return 匹配到的国际化信息
     * @exception NoSuchMessageException 当配置文件中找不到对应的消息时抛出此异常
     */
    @NotNull
    @Contract(value = "null,_ -> fail", pure = true)
    public String getMessage(String key, @Nullable Object... args) throws NoSuchMessageException {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

}
