package com.ailu.service.user;

import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.UserActiveDTO;
import com.ailu.entity.UserActive;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/24 上午12:31
 */

public interface UserActiveService {
    UserActive getUserActive(Long userId);

    void follow(UserActiveDTO followDTO);


    List<FolFanDTO> pageQueryFolOrFan(int folOrFan,int pageSize,int pageNum);
}
