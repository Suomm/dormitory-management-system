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
import org.jetbrains.annotations.Nullable;

/**
 * 文本相关操作的工具类。
 *
 * @author 王帅
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TextUtil {

    /**
     * 汉字数字字符。
     */
    private static final char[] NUMBERS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    /**
     * 将一百以内的数字转换为汉字形式。
     *
     * @param number 数字
     * @return 如果参数 {@code number > 100} 则返回 {@code null}
     */
    @Nullable
    @Contract(pure = true)
    public static String format(int number) {
        // 最大支持转换的数字
        int max = 100;
        if (number >= max) { return null; }
        // 所转换的数字小于零，可以直接取值使用
        int min = 10;
        if (number < min) { return String.valueOf(NUMBERS[number]); }
        // 正常情况下的两位数字转换为中文
        StringBuilder builder = new StringBuilder();
        int unit = number / 10;
        if (unit != 1) { builder.append(NUMBERS[unit]); }
        builder.append('十').append(NUMBERS[number % 10]);
        return builder.toString();
    }

}
