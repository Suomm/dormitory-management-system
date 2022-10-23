/*
 * Copyright (C) 2020-2022 the original author or authors.
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

package cn.edu.tjnu.hxxy.dms.validation;

import cn.edu.tjnu.hxxy.dms.validation.constraints.Type;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.stream.IntStream;

/**
 * 用于校验 {@link Integer} 表示的类型是否符合要求。
 *
 * @author 王帅
 * @since 1.0
 */
public class TypeValidator implements ConstraintValidator<Type, Integer> {

    private int[] ints;

    @Override
    public void initialize(Type constraintAnnotation) {
        this.ints = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return value != null && IntStream.of(ints).anyMatch(i -> i == value);
    }

}
