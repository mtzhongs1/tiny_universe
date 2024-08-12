package com.ailu.dto.shuo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/11 下午2:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShuoDTO {
    @NotNull(message = "内容不能为空")
    @Max(value = 500, message = "")
    private String content;
    @NotNull(message = "文章可公开状态不能为空")
    private Integer isPublic;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
