package com.ailu.vo.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/28 下午11:35
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO implements Serializable{
    private Long id;
    private Long userId;
    private String author;
    private String content;
    private String title;
    private String description;
    private String cover;
    private List<String> tag;
    private Boolean status;
    private Boolean type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
