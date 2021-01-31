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

package xyz.tran4f.dms.utils;

import org.jetbrains.annotations.Contract;

/**
 * <p>
 * 文本操作工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class TextUtils {

    private TextUtils() {
    }

    /**
     * <p>
     * 汉字数字字符。
     * </p>
     */
    private static final char[] NUMBERS = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};

    /**
     * <p>
     * 将一百以内的数字转换为汉字形式。
     * </p>
     *
     * @param number 数字
     * @return 汉字
     */
    @Contract(pure = true)
    public static String format(int number) {
        if (number > 100) { return null; }
        if (number < 10) { return String.valueOf(NUMBERS[number]); }
        StringBuilder builder = new StringBuilder();
        int unit = number / 10;
        if (unit != 1) { builder.append(NUMBERS[unit]); }
        builder.append('十').append(NUMBERS[number % 10]);
        return builder.toString();
    }

}
