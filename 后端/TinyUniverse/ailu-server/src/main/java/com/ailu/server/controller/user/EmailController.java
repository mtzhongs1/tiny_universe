package com.ailu.server.controller.user;

import com.ailu.aop.Log;
import com.ailu.aop.OperationType;
import com.ailu.result.Result;
import com.ailu.server.service.user.EmailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:50
 */

@RestController
@RequestMapping("/email")
@Api(tags = "邮件系统")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @PostMapping("/sendMailCode")
    @ApiOperation(value = "发送邮件验证码")
    @Log(title = "发送验证码", operationType = OperationType.OTHER)
    public Result sendMailCode(@RequestParam("to") String to) {
        emailService.sendEmailCode(to);
        return Result.success();
    }
    @PostMapping("/verifyMailCode")
    @ApiOperation(value = "验证邮件验证码")
    public Result verifyMailCode(@RequestParam String email,@RequestParam String code) {
        emailService.checkCode(email, code);
        return Result.success();
    }
}
