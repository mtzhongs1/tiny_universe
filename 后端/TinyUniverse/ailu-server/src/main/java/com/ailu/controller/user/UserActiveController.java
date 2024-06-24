package com.ailu.controller.user;

import com.ailu.entity.UserActive;
import com.ailu.result.Result;
import com.ailu.service.UserActiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/24 上午12:25
 */
@RestController
@RequestMapping("/user_active")
@Api(tags="用户活动管理")
public class UserActiveController {

    @Autowired
    private UserActiveService userActiveService;

    @GetMapping("/{userId}")
    @ApiOperation("获取用户活动属性")
    public Result getUserActive(@PathVariable Long userId){
        UserActive userActive = userActiveService.getUserActive(userId);
        return Result.success(userActive);
    }

    @PostMapping("/follow/{to}")
    @ApiOperation("关注")
    public Result follow(@PathVariable Long to){
        userActiveService.follow(to);
        return Result.success();
    }
}
