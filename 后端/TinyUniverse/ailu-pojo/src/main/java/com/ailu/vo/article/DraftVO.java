package com.ailu.vo.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/26 上午1:10
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DraftVO implements Serializable {
    private String title;
    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
