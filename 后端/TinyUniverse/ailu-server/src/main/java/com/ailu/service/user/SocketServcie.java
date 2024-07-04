package com.ailu.service.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.dto.user.UserSocketPageDTO;
import com.ailu.vo.user.DataAndCnt;
import com.ailu.vo.user.UserSocketVO;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 上午10:49
 */

public interface SocketServcie {
    void sendAll(MessageDTO message);

    DataAndCnt<UserSocketVO> getUsers(UserSocketPageDTO userSocketPageDTO);

    void addUser(Long userId);

    void deleteUser(Long userId);
}
