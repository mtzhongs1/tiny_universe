package com.ailu.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/27 上午1:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActiveDTO {
    Long userId;
    Long toUserId;

    Boolean isFollow;
}
