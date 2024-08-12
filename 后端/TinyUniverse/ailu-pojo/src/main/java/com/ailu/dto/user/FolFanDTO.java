package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/28 上午1:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolFanDTO {
    Long id;
    String username;
    String avatar;
    Boolean isFollow;
}
