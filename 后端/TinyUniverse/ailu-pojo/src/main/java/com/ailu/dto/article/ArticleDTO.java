package com.ailu.dto.article;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleDTO {
    @NotNull(message = "文章id不能为空")
    private Long id;
    @NotBlank(message = "内容不能为空")
    private String content;
    @NotBlank(message = "标题不能为空")
    @Size(min = 1,max = 20,message = "字符长度在1-20之间")
    private String title;
    private String description;
    private String cover;
    private LocalDateTime updateTime;

}
