package com.ailu.vo.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/20 下午11:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO {
    private Long id;
    private String description;
    private String cover;
    private String title;
    private String author;
    private LocalDateTime createTime;
    // active属性
    private Long love;
    private Long commentCount;
    private Long collectionCount;
    private Long watch;

    private Boolean isLove;
    private Boolean isCollection;

}
