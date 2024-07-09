package com.ailu.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/6 上午1:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private Long userId;
    private Long ArticleId;
    private Long parentId;
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1,max = 500,message = "评论内容长度在1-200之间")
    private String content;
    private Long createTime;
    private Long updateTime;
    private Long love;
}
