package com.ailu.controller.user;

import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.FolFanPageDTO;
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
    public Result<UserActive> getUserActive(@PathVariable Long userId){
        UserActive userActive = userActiveService.getUserActive(userId);
        return Result.success(userActive);
    }

    @PostMapping("/follow")
    @ApiOperation("关注")
    public Result follow(@RequestBody UserActiveDTO userActiveDTO){
        userActiveService.follow(userActiveDTO);
        return Result.success();
    }

    @GetMapping("/follow/page")
    @ApiOperation("分页查询关注数")
    public Result<PageResult> pageQueryFollow(FolFanPageDTO folFanPageDTO){
        PageResult page = userActiveService.pageQueryFolOrFan(1,folFanPageDTO);
        return Result.success(page);
    }

    @GetMapping("/fan/page")
    @ApiOperation("分页查询粉丝")
    public Result<PageResult> pageQueryFans(FolFanPageDTO folFanPageDTO){
        PageResult page= userActiveService.pageQueryFolOrFan(0,folFanPageDTO);
        return Result.success(page);
    }

}
