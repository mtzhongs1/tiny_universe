package com.ailu.dto.user;

import com.ailu.entity.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 5/6/2024 下午12:38
 */

@Data
public class UserUpdateDTO {
    private Long id;
    @NotBlank(message = "用户名不能为空")
    @Size(max = 15, message = "用户名不能超过15个字符")
    private String username;
    private Integer sex;
    private String avatar;
    private LocalDate birthday;
    private String description;
    private LocalDateTime updateTime;
}
