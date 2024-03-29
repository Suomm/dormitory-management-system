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

package cn.edu.tjnu.hxxy.dms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 验证码实体类。
 *
 * @author 王帅
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Captcha implements Serializable {

    private static final long serialVersionUID = -4483861236935426035L;

    /**
     * 验证码的文本内容。
     */
    private String code;

    /**
     * 验证码的过期时间。
     */
    private long outDate;

}
