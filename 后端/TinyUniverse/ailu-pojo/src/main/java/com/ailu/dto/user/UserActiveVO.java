package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/7 下午10:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActiveVO {
    private String avatar;
    private Long userId;
    private Long fans;
    private Long follows;
}
