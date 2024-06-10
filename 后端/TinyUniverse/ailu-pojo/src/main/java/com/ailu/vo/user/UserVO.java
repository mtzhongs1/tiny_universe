package com.ailu.vo.user;

import lombok.Data;

import java.time.LocalDate;

/**
 * @Description:
 * @Author: ailu
 * @Date: 7/6/2024 下午2:29
 */

@Data
public class UserVO {
    private String username;
    private String email;
    private Character sex;
    private String avatar;
    private String description;
    private LocalDate birthday;
}
