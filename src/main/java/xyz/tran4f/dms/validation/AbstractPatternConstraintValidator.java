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

import xyz.tran4f.dms.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * <p>
 * 正则表达式约束的检验器抽象父类。只提供一个用于校验的正则表达式，即可完成数据的校验。
 * 被校验的对象不能为 {@code null} 并且不能为空白。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public abstract class AbstractPatternConstraintValidator<A extends Annotation> implements ConstraintValidator<A, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StringUtils.notBlank(value) && value.matches(regex());
    }

    /**
     * <p>
     * 用于校验数据的正则表达式。
     * </p>
     *
     * @return 正则表达式
     */
    public abstract String regex();

}
