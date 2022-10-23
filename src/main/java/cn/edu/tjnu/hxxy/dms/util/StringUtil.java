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

package cn.edu.tjnu.hxxy.dms.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * 处理字符串类型数据的工具类。
 *
 * @author 王帅
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtil {

    public static final String EMPTY = "";

    /**
     * 判断一个字符串是否为空白（若果参数为 {@code null} 则返回 {@code false}）。
     *
     * @param value 需要判断的值
     * @return 是否为空或者空字符串
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean notBlank(String value) {
        return value != null && value.trim().length() > 0;
    }

}
