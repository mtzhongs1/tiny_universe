package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/16 下午8:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem {
    private String content;
    private String a;
    private String b;
    private String c;
    private String t;
    private String p;
}
