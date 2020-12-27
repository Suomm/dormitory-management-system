/*
 * Copyright (C) 2020 Wang Shuai (suomm.macher@foxmail.com)
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

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author 王帅
 * @since 1.0
 */
public final class MD5Utils {

    private MD5Utils() {
    }

    public static String encode(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean matches(String raw, String encoded) {
        return encode(raw).equals(encoded);
    }
}
