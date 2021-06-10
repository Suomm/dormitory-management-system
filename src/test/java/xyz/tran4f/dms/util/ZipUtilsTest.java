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
 * {@link ZipUtils} 的测试类。
 *
 * @author 王帅
 * @since 1.0
 * @see ZipUtils
 */
public class ZipUtilsTest {

    @Test
    public void compress() throws IOException {
        ZipUtils.compress("D:/Download/Compressed/dms_test.zip",
                "D:/Download/Compressed/小恐龙公文排版助手1880 EXE版",
                "D:/Download/Compressed/1-160314143119.rar");
    }

}