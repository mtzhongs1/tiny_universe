package com.ailu.server.service.user;

import com.ailu.dto.user.UserLoginDTO;
import com.ailu.dto.user.UserRegisterDTO;
import com.ailu.dto.user.UserUpdateDTO;
import com.ailu.result.PageResult;
import com.ailu.vo.user.UserVO;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:47
 */

public interface UserService {
    String login(UserLoginDTO userLoginDTO);

    void register(UserRegisterDTO userRegisterDTO);

    void updateMsg(UserUpdateDTO userUpdateMsgDTO);


    UserVO getUser(Long userId);

    void updatePwd(String email,String password);

    PageResult search(String content, Integer pageNum, Integer pageSize, Integer type);
}
