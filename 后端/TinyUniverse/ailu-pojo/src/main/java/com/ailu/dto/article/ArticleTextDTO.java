package com.ailu.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/5 上午12:19
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleTextDTO {
    private Long id;
    private String content;
    private String author;
    private String title;
}
