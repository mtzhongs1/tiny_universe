package com.ailu.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/23 下午10:07
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionDTO {
    private Long id;
    @NotBlank(message = "名称不能为空")
    @Size(min = 1,max = 20,message = "字符长度在1-20之间")
    private String name;
    private Integer type;
    @NotNull(message = "请选择是否公开")
    private Integer isPublic;
    private Long userId;
    private Long articleId;
    private Long parentId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<Long> parentIds;

    public CollectionDTO(String name, Integer type, Integer isPublic, Long userId, Long articleId, List<Long> parentIds) {
        this.name = name;
        this.type = type;
        this.isPublic = isPublic;
        this.userId = userId;
        this.articleId = articleId;
        this.parentIds = parentIds;
    }
}
