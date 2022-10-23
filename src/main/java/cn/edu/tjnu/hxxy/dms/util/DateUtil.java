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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * 处理日期的工具类。
 *
 * @author 王帅
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DateUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneOffset OFFSET = ZoneOffset.of("+8");
    private static final long MILLISECOND = 1000L * 60 * 60 * 24 * 7;

    /**
     * 按照 {@code yyyy-MM-dd} 的格式格式化日期。
     *
     * @return 日期格式化字符串
     */
    public static String now() {
        return FORMATTER.format(LocalDate.now());
    }

    /**
     * 将毫秒数转化为 {@code yyyy-MM-dd} 格式的字符串。
     *
     * @param epochMilli 毫秒数
     * @return 格式化的日期
     */
    public static String format(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), OFFSET).format(FORMATTER);
    }

    /**
     * 截取当前年份的前两位数。
     *
     * @return 世纪数减一
     */
    public static String subYear() {
        return Integer.toString(LocalDate.now().getYear()).substring(0, 2);
    }

    /**
     * 将 {@code yyyy-MM-dd} 类型的日期字符串转换为毫秒数。
     *
     * @param text 格式化的日期
     * @return 日期对应的毫秒数
     */
    public static long parse(CharSequence text) {
        return FORMATTER.parse(text, LocalDate::from).atStartOfDay().toInstant(OFFSET).toEpochMilli();
    }

    /**
     * 计算并返回当前周在本学期的周次。
     *
     * @param begin 学期开始日期
     * @return 当前周周次
     */
    public static int weekOfSemester(long begin) {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(begin);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.add(Calendar.DATE, -calendar.get(Calendar.DAY_OF_WEEK));
        begin = calendar.getTime().getTime();
        return (int) ((now - begin) / MILLISECOND + 1);
    }

}
