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

package xyz.tran4f.dms.attribute;

/**
 * <p>
 * 用于数据校验的正则表达式属性。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class RegexAttribute {


    private RegexAttribute() {
    }

    public static final String ID = "\\d{2}3007\\d{4}";
    public static final String GRADE = "\\d{4}";
    public static final String PASSWORD = "^[\\S]{6,32}$";

}
