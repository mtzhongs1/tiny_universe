package com.ailu.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/15 下午5:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleModifyDTO {
    private String content;
    private String type;
}
