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

import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * <p>
 * 有关 MD5 加密的相关工具。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class MD5Utils {

    private MD5Utils() {
    }

    /**
     * <p>
     * 将指定字符串进行 MD5 加密。
     * </p>
     *
     * @param raw 未加密的字符串
     * @return MD5 加密过的字符串
     */
    public static String encode(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * <p>
     * 判断明文与密文是否一致（忽略大小写）。
     * </p>
     *
     * @param raw 未加密的字符串
     * @param encoded 加密过的字符串
     * @return 密文是否为原先字符串加密而成的
     */
    public static boolean matches(String raw, String encoded) {
        return encode(raw).equalsIgnoreCase(encoded);
    }

}
