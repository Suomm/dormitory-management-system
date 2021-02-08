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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * <p>
 * 处理日期的工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class DateUtils {

    private DateUtils() {
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final ZoneOffset OFFSET = ZoneOffset.of("+8");
    private static final long MILLISECOND = 1000 * 60 * 60 * 24 * 7;

    /**
     * <p>
     * 按照<b>yyyy-MM-dd</b>的格式格式化日期。
     * </p>
     *
     * @return 日期格式化字符串
     */
    public static String now() {
        return FORMATTER.format(LocalDate.now());
    }

    public static String format(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), OFFSET).format(FORMATTER);
    }

    /**
     * <p>
     * 截取当前年份的前两位数。
     * </p>
     *
     * @return 世纪数减一
     */
    public static String subYear() {
        return Integer.toString(LocalDate.now().getYear()).substring(0, 2);
    }

    public static long parse(CharSequence text) {
        return FORMATTER.parse(text, LocalDate::from).atStartOfDay().toInstant(OFFSET).toEpochMilli();
    }

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
