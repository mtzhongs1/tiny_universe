package com.ailu.service;

import com.ailu.entity.UserActive;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/6/24 上午12:31
 */

public interface UserActiveService {
    UserActive getUserActive(Long userId);

    void follow(Long to);
}
