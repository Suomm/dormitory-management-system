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

package xyz.tran4f.dms.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.List;
import java.util.Locale;

/**
 * Spring MVC 配置类。
 *
 * @author 王帅
 * @since 1.0
 */
@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {

    /**
     * Spring security 中配置 {@link HttpSessionEventPublisher} 防用户重复登录，
     * 解决设置最大登录用户数量后注销不了的问题。
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> listenerRegister() {
        ServletListenerRegistrationBean<HttpSessionEventPublisher> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new HttpSessionEventPublisher());
        return srb;
    }

    // 国际化的有关配置

    /**
     * 通过{@link SessionLocaleResolver}检验用户会话中预置的属性来解析区域。
     * 如果该会话属性不存在，它会根据 accept-language HTTP头部确定默认区域。
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return slr;
    }

    /**
     * 如果想要用户能改变 {@link java.util.Locale}, 我们需要配置 {@link LocaleChangeInterceptor}。
     * 这个拦截器将检查传入的请求，如果请求中有 {@code local} 的参数，如 {@code http://localhost:8080/test?local=zh_CN}，
     * 则该拦截器将改变当前用户的 {@link java.util.Locale}。
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        return new LocaleChangeInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 LocaleChangeInterceptor 拦截器
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ResourceHttpMessageConverter());
    }

}
