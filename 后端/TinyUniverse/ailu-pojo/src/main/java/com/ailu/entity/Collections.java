package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/23 下午7:34
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Collections {
    private Long id;
    private String name;
    private Integer type;
    private Integer isPublic;
    private Long userId;
    private Long articleId;
    private Long parentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
