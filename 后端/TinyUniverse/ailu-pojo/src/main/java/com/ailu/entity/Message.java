package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午2:45
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private Long userId;
    private String username;
    private String avatar;
    private String text;
    private LocalDateTime data;
}
