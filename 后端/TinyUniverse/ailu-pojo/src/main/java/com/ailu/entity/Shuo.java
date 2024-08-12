package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/11 下午1:34
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shuo {
    private Long id;
    private String content;
    private Integer isPublic;
    private Long userId;
    private Long love;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
