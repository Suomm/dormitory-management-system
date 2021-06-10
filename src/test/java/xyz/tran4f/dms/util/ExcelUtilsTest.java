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

package xyz.tran4f.dms.util;

import org.junit.jupiter.api.Test;
import xyz.tran4f.dms.model.Dormitory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ExcelUtils} 的测试类。
 *
 * @author 王帅
 * @since 1.0
 * @see ExcelUtils
 */
public class ExcelUtilsTest {

    @Test
    public void excel2Image() throws IOException {
        List<Dormitory> clean = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            Dormitory dormitory = new Dormitory();
            dormitory.setGrade("202" + i + "级").setRoom(i + "-20" + i);
            clean.add(dormitory);
        }
        List<Dormitory> dirty = new ArrayList<>(14);
        for (int i = 0; i < 14; i++) {
            Dormitory dormitory = new Dormitory();
            dormitory.setGrade("202" + i + "级").setRoom(i + "-20" + i);
            dirty.add(dormitory);
        }
        ExcelUtils.data2Image("D:/Download/Roaming/test.png", clean, dirty);
    }

}