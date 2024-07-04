package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午5:10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSocketDTO {
    private Long id;
    private String username;
    private String avatar;
}
