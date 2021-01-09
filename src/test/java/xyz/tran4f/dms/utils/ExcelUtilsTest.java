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
import xyz.tran4f.dms.pojo.Dormitory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 2021/1/7
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public class ExcelUtilsTest {

    @Test
    public void excel2Image() throws IOException {
        List<Dormitory> first = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            Dormitory dormitory = new Dormitory();
            dormitory.setGrade("202" + i + "级").setRoom(i + "-20" + i);
            first.add(dormitory);
        }

        List<Dormitory> last = new ArrayList<>(14);
        for (int i = 0; i < 14; i++) {
            Dormitory dormitory = new Dormitory();
            dormitory.setGrade("202" + i + "级").setRoom(i + "-20" + i);
            last.add(dormitory);
        }
        ExcelUtils.data2Image("D:\\test.png", first, last);
    }

}