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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>
 * Wrapper 条件构造工具类。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class WrapperUtils {

    private WrapperUtils() {
    }

    /**
     * <p>
     * 通过对象构建查询条件。
     * </p>
     *
     * @param value 对象
     * @param <T> 类型
     * @return 查询封装
     */
    @SneakyThrows
    public static <T> QueryWrapper<T> allEq(T value) {
        Class<?> clazz = value.getClass();
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Method method;
            try {
                method = clazz.getDeclaredMethod("get" + captureName(name));
            } catch (NoSuchMethodException | SecurityException e) {
                continue;
            }
            Object invoke = method.invoke(value);
            if (invoke != null) {
                // 空的字符串类型
                if (invoke instanceof String && ((String) invoke).isEmpty()) {
                    continue;
                }
                // 含有封装的值
                wrapper.eq(name, invoke);
            }
        }
        return wrapper;
    }

    /**
     * <p>
     * 将字符串的首字母转大写。
     * </p>
     *
     * @param str 需要转换的字符串
     * @return 转换后的字符串
     */
    private static String captureName(String str) {
        // 进行字母的 Ascii 编码前移，效率要高于截取字符串进行转换的操作
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

}
