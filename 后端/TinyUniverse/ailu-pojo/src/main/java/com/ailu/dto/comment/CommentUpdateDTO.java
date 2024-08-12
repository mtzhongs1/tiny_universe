package com.ailu.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/16 下午10:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateDTO {
    @NotNull(message = "id不能为空")
    private Long id;
    @NotNull(message = "文章id不能为空")
    private Long articleId;
    private String content;
    private Long updateTime;
}
