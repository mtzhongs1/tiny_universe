package com.ailu.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/6 上午1:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    @NotNull(message = "文章id不能为空")
    private Long ArticleId;
    private Long parentId;
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1,max = 500,message = "评论内容长度在1-500之间")
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long love;
}
