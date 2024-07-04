package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 上午10:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long userId;
    private String username;
    private String avatar;
    private String text;
    private LocalDateTime date;
}
