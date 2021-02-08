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

import xyz.tran4f.dms.attribute.RegexAttribute;
import xyz.tran4f.dms.validation.constraints.Grade;

/**
 * <p>
 * 用于校验年级是否符合要求。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 * @see RegexAttribute#GRADE
 */
public class GradeValidator extends AbstractPatternConstraintValidator<Grade> {

    @Override
    public String regex() {
        return RegexAttribute.GRADE;
    }

}
