package com.ailu.server.service.user;

import com.ailu.dto.user.FolFanPageDTO;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/24 上午12:31
 */

public interface UserActiveService {
    UserActive getUserActive(Long userId);

    void follow(Long toUserId,Boolean isFollow);


    PageResult pageQueryFolOrFan(int folOrFan, FolFanPageDTO folFanPageDTO);

    Boolean isFollow(Long followId);

}
