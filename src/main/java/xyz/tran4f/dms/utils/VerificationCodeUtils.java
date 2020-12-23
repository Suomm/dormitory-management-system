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

import xyz.tran4f.dms.pojo.VerificationCode;

import java.util.Random;

/**
 * 验证码生成器。
 *
 * @author 王帅
 * @since 1.0
 */
public final class VerificationCodeUtils {

    public static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
    };
    public static final int LENGTH = CHARS.length;
    public static final Random RANDOM = new Random();

    private VerificationCodeUtils() {
    }

    public static VerificationCode getVerificationCode(int length, long time) {
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = CHARS[RANDOM.nextInt(LENGTH)];
        }
        String code = new String(ret);
        long outDate = System.currentTimeMillis() + time;
        return new VerificationCode(code, outDate);
    }

}
