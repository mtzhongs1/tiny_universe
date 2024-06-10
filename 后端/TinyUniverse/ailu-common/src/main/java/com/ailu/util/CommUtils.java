package com.ailu.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommUtils {

    public static String getNowDateLongStr(String format) {
        // 创建日期时间格式化对象
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        // 获取当前日期时间
        LocalDateTime now = LocalDateTime.now();
        // 使用格式化对象将日期时间转换为指定格式的字符串
        String dateString = now.format(formatter);
        // 返回格式化后的字符串
        return dateString;
    }
}
