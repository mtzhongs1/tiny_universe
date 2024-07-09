package com.ailu.server.controller.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.dto.user.UserSocketPageDTO;
import com.ailu.result.Result;
import com.ailu.server.service.user.SocketServcie;
import com.ailu.vo.user.DataAndCnt;
import com.ailu.vo.user.UserSocketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 上午10:45
 */
@RequestMapping("/socket")
@RestController
public class SocketController {

    @Autowired
    private SocketServcie socketServcie;

    @PostMapping("/sendAll")
    public Result sendAll(@RequestBody MessageDTO message){
        socketServcie.sendAll(message);
        return Result.success();
    }

    @GetMapping("/users")
    public Result<DataAndCnt<UserSocketVO>> getUsers(UserSocketPageDTO userSocketPageDTO){
        DataAndCnt<UserSocketVO> users = socketServcie.getUsers(userSocketPageDTO);
        return Result.success(users);
    }
}
