package com.ailu.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftDTO {
    private Long userId;
    private Integer pageNum;
    private Integer pageSize;
    private String name = "";
}
