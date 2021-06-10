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

package xyz.tran4f.dms.constant;

import java.util.regex.Pattern;

/**
 * 用于数据校验的正则表达式。
 *
 * @author 王帅
 * @since 1.0
 */
public final class RegexConsts {

    private RegexConsts() {
    }

    /**
     * 用于校验天津师范大学化学学院的学生学号。
     */
    public static final Pattern ID = Pattern.compile("^\\d{2}3007\\d{4}$");

    /**
     * 用于校验天机师范大学学生公寓房间号。
     */
    public static final Pattern ROOM = Pattern.compile("^\\d{1,2}(?:-\\d)?-\\d{3}$");

    /**
     * 用于校验所属年级。
     */
    public static final Pattern GRADE = Pattern.compile("^\\d{4}级$");

    /**
     * 用于校验天津师范大学学生公寓名称。
     */
    public static final Pattern BUILDING = Pattern.compile("^学生公寓\\d{1,2}号楼$");

    /**
     * 用于校验密码，六到三十二位非空白。
     */
    public static final Pattern PASSWORD = Pattern.compile("^[\\S]{6,32}$");

}
