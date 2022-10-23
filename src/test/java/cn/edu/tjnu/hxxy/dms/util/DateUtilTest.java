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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

/**
 * {@link DateUtil} 的测试类。
 *
 * @author 王帅
 * @see DateUtil
 * @since 1.0
 */
class DateUtilTest {

    @Test
    void weekOfSemester() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, Calendar.JANUARY, 1);
        long start = calendar.getTime().getTime();
        Integer weekOfSemester = Assertions.assertDoesNotThrow(() -> DateUtil.weekOfSemester(start));
        System.out.println(weekOfSemester);
    }

    @Test
    void parse() {
        String formatDate = "2020-02-02";
        Assertions.assertEquals(formatDate, DateUtil.format(DateUtil.parse(formatDate)));
    }

}