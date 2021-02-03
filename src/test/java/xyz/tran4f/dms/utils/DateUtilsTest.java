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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import xyz.tran4f.dms.attribute.RedisAttribute;

import javax.xml.transform.Source;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;

import static xyz.tran4f.dms.attribute.RedisAttribute.KEY_DRAFT;

/**
 * <p>
 * 2021/1/29
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class DateUtilsTest {

    @Test
    public void weekOfSemester() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long start = format.parse("2020-12-31").getTime();
        System.out.println(DateUtils.weekOfSemester(start));
    }

    @Test
    public void parse() {
        System.out.println(DateUtils.format(DateUtils.parse("2020-02-02")));
    }

}