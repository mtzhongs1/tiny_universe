package com.ailu.vo.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午3:34
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSocketVO implements Serializable {
    private Long id;
    private String username;
    private String avatar;
}
