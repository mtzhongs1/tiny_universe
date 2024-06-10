package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:53
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Email {
    private String to;
    private String message;
    private String subject;
}
