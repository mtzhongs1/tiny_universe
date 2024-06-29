package com.ailu.vo.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftDetailVO {
    private String title;
    private Long id;
    private String cover;
    private String content;
    private String tag;
    private String description;
}
