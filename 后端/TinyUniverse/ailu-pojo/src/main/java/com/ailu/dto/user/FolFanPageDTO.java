package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/30 上午12:29
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolFanPageDTO {
    private Long userId;
    private Integer pageSize;
    private Integer pageNum;
}
