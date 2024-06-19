package com.ailu.dto.article;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDTO {
    private Long userId;
    @NotBlank(message = "内容不能为空")
    private String content;
    @NotBlank(message = "标题不能为空")
    private String title;
    private String description;
    private String cover;
    @NotBlank(message = "请至少选择一个标签")
    private String tag;
    @NotNull(message = "类型不能为空")
    private Boolean type;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
