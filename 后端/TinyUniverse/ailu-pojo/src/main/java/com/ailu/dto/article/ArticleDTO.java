package com.ailu.dto.article;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDTO {
    @NotNull(message = "文章id不能为空")
    private Long id;
    @NotBlank(message = "内容不能为空")
    private String content;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String description;
    private String cover;
    private LocalDateTime updateTime;

}
