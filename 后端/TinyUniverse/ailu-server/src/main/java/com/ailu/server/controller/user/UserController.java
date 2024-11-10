package com.ailu.server.controller.user;

import com.ailu.aop.Log;
import com.ailu.aop.OperationType;
import com.ailu.dto.user.UserLoginDTO;
import com.ailu.dto.user.UserRegisterDTO;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.server.dataSource.searchType.DataSourceFactory;
import com.ailu.server.service.user.UserService;
import com.ailu.vo.user.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:40
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户系统")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @ApiOperation("登录")
    @PostMapping("/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO){
        String token = userService.login(userLoginDTO);
        return Result.success(token);
    }
    @ApiOperation("注册")
    @PostMapping("/register")
    public Result register(@RequestBody UserRegisterDTO userRegisterDTO){
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @ApiOperation("获取用户信息")
    @GetMapping
    @Cacheable(value = "user", key = "#userId", condition = "#userId != null")
    public Result<UserVO> getUser(Long userId){
        UserVO userVO = userService.getUser(userId);
        return Result.success(userVO);
    }


    @ApiOperation("修改用户信息")
    @PutMapping("/updateMsg")
    // @CacheEvict(value = "user",key = "#userUpdateDTO.id")
    @Log(title="个人信息", operationType = OperationType.UPDATE)
    public Result updateMsg(@RequestBody UserUpdateDTO userUpdateDTO){
        userService.updateMsg(userUpdateDTO);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/updatePwd")
    public Result updatePwd(@RequestParam String email, @RequestParam String password){
        userService.updatePwd(email,password);
        return Result.success();
    }

    @ApiOperation("根据用户名模糊查询用户")
    @GetMapping("/search")
    public Result search(@RequestParam String content, @RequestParam int pageNum, @RequestParam int pageSize){
        PageResult user = dataSourceFactory.getDataSource("user").search(content, pageNum, pageSize, null);
        return Result.success(user);
    }
}
