package com.ailu.dto.collection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/8/8 上午2:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColDTO implements Serializable {
    private static final long serialVersionUID =  1256541213551L;
    private Long articleId;
    private String parentIds;
    private String name;
    private Integer type = 0;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
