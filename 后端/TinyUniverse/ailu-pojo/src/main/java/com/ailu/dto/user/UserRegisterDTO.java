package com.ailu.dto.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午8:20
 */

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 15, message = "用户名不能超过15个字符")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 2, max = 20, message = "密码长度在2到15个字符之间")
    private String password;
    @NotBlank(message = "验证码不能为空")
    private String code;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
