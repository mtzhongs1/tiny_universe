package com.ailu.service.user;

import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.FolFanPageDTO;
import com.ailu.dto.user.UserActiveDTO;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/24 上午12:31
 */

public interface UserActiveService {
    UserActive getUserActive(Long userId);

    void follow(UserActiveDTO followDTO);


    PageResult pageQueryFolOrFan(int folOrFan, FolFanPageDTO folFanPageDTO);
}
