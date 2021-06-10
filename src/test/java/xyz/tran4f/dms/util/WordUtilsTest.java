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

import java.io.IOException;

/**
 * {@link WordUtils} 的测试类。
 *
 * @author 王帅
 * @since 1.0
 * @see WordUtils
 */
public class WordUtilsTest {

    @Test
    public void create() throws IOException {
        WordUtils.create("D:/Download/Documents/化学学院第一周宿舍文明检查周报.docx",
                "【化学学院第十四周宿舍文明检查周报】",
                "近日，我院面向本科生，对学生公寓进行了宿舍安全文明检查。本次检查共评出“优秀宿舍”和“问题宿舍”若干，具体如下：",
                "在本次检查过程中，并未发现任何大功率电器、烟、酒等违禁物品，学生们高度重视宿舍安全问题，遵守公寓安全守则，在此特予表扬。本次的“优秀宿舍”做到地面、桌面整洁干净无杂物，私人物品摆放整齐有序，无过多悬挂物，营造出良好的宿舍氛围。但依旧有“问",
                "希望同学们养成良好的个人习惯，整理好自己的个人物品，轮流安排值日，宿舍全员共同保持宿舍的干净整洁，共建温馨安全干净的学习和休息的空间。",
                "温馨提示：天气最近转凉，请各位同学根据天气变化及时增添衣物。冬季来临，请各位同学注意宿舍的开窗通风，防止冬季传染病。临近期末考试，请大家注意室内和室外的温差，因温度及时增减衣物，");
    }

}