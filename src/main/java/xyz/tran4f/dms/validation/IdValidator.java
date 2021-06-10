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

package xyz.tran4f.dms.validation;

import xyz.tran4f.dms.constant.RegexConsts;
import xyz.tran4f.dms.validation.constraints.Id;

import java.util.regex.Pattern;

/**
 * 用于校验学号是否符合要求。
 *
 * @author 王帅
 * @since 1.0
 * @see RegexConsts#ID
 */
public class IdValidator extends AbstractPatternConstraintValidator<Id> {

    @Override
    public Pattern regex() {
        return RegexConsts.ID;
    }

}
