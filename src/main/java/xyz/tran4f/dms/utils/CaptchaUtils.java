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

import xyz.tran4f.dms.exception.CaptchaException;
import xyz.tran4f.dms.pojo.Captcha;

import java.util.Random;

import static xyz.tran4f.dms.attribute.ExceptionAttribute.*;

/**
 * <p>
 * 验证码相关操作的工具类，包括生成和校验验证码。
 * </p>
 *
 * @author 王帅
 * @since 1.0
 */
public final class CaptchaUtils {

    /**
     * 生成验证码所需要的字符，包括：
     * <ul>
     *     <li>数字：0-9</li>
     *     <li>小写字母：a-z</li>
     *     <li>大写字母：A-Z</li>
     * </ul>
     */
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
    };

    /**
     * 所需字符数组的长度，用于生成随机数的边界。
     */
    private static final int LENGTH = CHARS.length;

    private CaptchaUtils() {
    }

    /**
     * <p>
     * 根据指定的长度生成一个随机包含数字、小写字母、大写字母的验证码，并保存
     * 该验证码的过期时间。
     * </p>
     *
     * @param length 生成验证码的长度
     * @param time 验证码过期时间
     * @return 验证码封装类
     */
    public static Captcha getCaptcha(int length, long time) {
        return new Captcha(getCode(length), System.currentTimeMillis() + time);
    }

    public static String getCode(int length) {
        // 随机数生成器，用于取出字符数组中的元素
        Random random = new Random();
        char[] ret = new char[length];
        for (int i = 0; i < length; i++) {
            ret[i] = CHARS[random.nextInt(LENGTH)];
        }
        return new String(ret);
    }

    /**
     * <p>
     * 检查之前生成的验证码与用户输入的验证码是否相同。
     * </p>
     *
     * @param before 之前保存的验证码
     * @param after 之后要比对的验证码
     * @exception CaptchaException 检查失败抛出此异常
     */
    public static void checkCaptcha(Captcha before, Captcha after) throws CaptchaException {
        // 验证码为空，用户还未发送验证码
        if (before == null) {
            throw new CaptchaException(USER_CAPTCHA_MISSING);
        }
        // 比对验证码过期时间
        if (before.getOutDate() <= after.getOutDate()) {
            throw new CaptchaException(USER_CAPTCHA_OVERDUE);
        }
        // 比对验证码是否正确
        if (!before.getCode().equals(after.getCode())) {
            throw new CaptchaException(USER_CAPTCHA_WRONG);
        }
    }

}
