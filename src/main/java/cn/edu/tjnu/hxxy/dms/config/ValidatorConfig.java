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

package cn.edu.tjnu.hxxy.dms.config;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * 数据校验配置类。
 *
 * @author 王帅
 * @since 1.0
 */
@Configuration(proxyBeanMethods = false)
public class ValidatorConfig {

    /**
     * 默认会校验完所有属性，然后将错误信息一起返回，但很多时候不需要这样，
     * 一个校验失败了，其它就不必校验了。
     */
    @Bean
    public Validator validator() {
        return Validation.byProvider(HibernateValidator.class)
                .configure()
                // 为提高性能，一检测到错误就停止校验。
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }

}