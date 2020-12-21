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

package xyz.tran4f.dms.config;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 *
 *
 * @author 王帅
 * @since 1.0
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    /**
     * 解决设置最大登录用户数量后注销不了的问题
     *
     * @return 注册器
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> listenerRegister() {
        ServletListenerRegistrationBean<HttpSessionEventPublisher> srb = new ServletListenerRegistrationBean<>();
        srb.setListener(new HttpSessionEventPublisher());
        return srb;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //配置映射关系
        //即：/webjars/** 映射到 classpath:/META-INF/resources/webjars/
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                //新增 resourceChain 配置即开启缓存配置。
                //不知道为何不加这个配置 设置了 webjars-locator 未生效。。没过多纠结。。
                .resourceChain(true);//生产时建议开启缓存（只是缓存了资源路径而不是资源内容）,开发是可以设置为false
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/manager/welcome.html").setViewName("/manager/welcome");
        registry.addViewController("/user/success.html").setViewName("/user/success");
    }

}
