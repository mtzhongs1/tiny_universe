package com.ailu.util;

import java.util.Random;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午5:39
 */

public class CodeUtil {
    private static final Random random = new Random();
    public static String generateCode4() {
        return String.valueOf(1000 + random.nextInt(9000));
    }
}
