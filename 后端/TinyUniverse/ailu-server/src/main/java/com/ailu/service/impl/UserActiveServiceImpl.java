package com.ailu.service.impl;

import com.ailu.entity.UserActive;
import com.ailu.mapper.UserActiveMapper;
import com.ailu.service.UserActiveService;
import com.ailu.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/24 上午12:31
 */
@Service
public class UserActiveServiceImpl implements UserActiveService {

    @Autowired
    private UserActiveMapper userActiveMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public UserActive getUserActive(Long userId) {
        String key = "user_active:userId:" + userId;
        Map<String, Object> userActive = redisCache.getCacheMap(key);
        if(userActive == null){
            userActive = new HashMap<>();
            redisCache.setCacheMap(key,userActive);
        }
        List<Long> follows = (List<Long>) userActive.get("follows");
        List<Long> fans = (List<Long>) userActive.get("fans");
        return new UserActive(userId,fans,follows);
    }

    @Override
    public void follow(Long to) {

    }
}
