package com.ailu.controller.user;

import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.UserActiveDTO;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.service.user.UserActiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Result follow(@RequestBody UserActiveDTO userActiveDTO){
        userActiveService.follow(userActiveDTO);
        return Result.success();
    }

    @GetMapping("/follow/{pageSize}/{pageNum}")
    @ApiOperation("分页查询关注数")
    public Result<PageResult> pageQueryFollow(@PathVariable int pageSize,@PathVariable int pageNum){
        List<FolFanDTO> follows = userActiveService.pageQueryFolOrFan(1,pageSize,pageNum);
        return Result.success();
    }

    @GetMapping("/fan")
    @ApiOperation("分页查询粉丝数")
    public Result<PageResult> pageQueryFans(@PathVariable int pageSize,@PathVariable int pageNum){
        List<FolFanDTO> fans = userActiveService.pageQueryFolOrFan(0,pageSize,pageNum);
        return Result.success();
    }

}
