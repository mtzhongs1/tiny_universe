package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/24 上午12:33
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActive {
    private Long userId;
    private Long fans;
    private Long follows;
}
