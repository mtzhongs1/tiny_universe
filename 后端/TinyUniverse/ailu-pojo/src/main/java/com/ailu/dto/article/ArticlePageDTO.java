package com.ailu.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 下午3:49
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlePageDTO implements Serializable {
    private static final long serialVersionUID = 1256541213553L;
    Long userId;
    @NotNull(message = "pageNum不能为空")
    Integer pageNum;
    @NotNull(message = "pageSize不能为空")
    Integer pageSize;
    Integer type;
    Integer tag;
}
