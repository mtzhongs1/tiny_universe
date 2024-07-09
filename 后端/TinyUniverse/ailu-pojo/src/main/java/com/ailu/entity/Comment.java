package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 下午12:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
    private Long userId;
    private Long ArticleId;
    private Long parentId;
    private String content;
    private Long createTime;
    private Long updateTime;
    private Long love;
}
