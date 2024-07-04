package com.ailu.dto.user;

import lombok.Data;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午4:18
 */
@Data
public class UserSocketPageDTO {
    private String username = "";
    private Integer pageSize;
}
