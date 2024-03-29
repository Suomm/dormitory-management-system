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

package cn.edu.tjnu.hxxy.dms;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Spring Boot 应用程序启动类。
 *
 * @author 王帅
 * @since 1.0
 */
@EnableScheduling
@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("cn.edu.tjnu.hxxy.dms.mapper")
@EnableGlobalMethodSecurity(securedEnabled = true)
public class DormitoryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DormitoryManagementSystemApplication.class, args);
    }

}
