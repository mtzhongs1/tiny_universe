package com.ailu.service.impl.user;

import com.ailu.context.BaseContext;
import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.FolFanPageDTO;
import com.ailu.dto.user.UserActiveDTO;
import com.ailu.entity.UserActive;
import com.ailu.mapper.UserActiveMapper;
import com.ailu.result.PageResult;
import com.ailu.service.user.UserActiveService;
import com.ailu.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
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
    private RedisCache redisCache;

    @Override
    public UserActive getUserActive(Long userId) {
        String key = "user_active:userId:" + userId;
        Map<String, Object> userActive = redisCache.getCacheMap(key);
        ListOperations listOperations = redisCache.redisTemplate.opsForList();
        if(userActive == null){
            userActive = new HashMap<>();
            redisCache.setCacheMap(key,userActive);
        }
        String folkey = "user:"+userId+"follow";
        String fanKey = "user:"+userId+"fan";

        Long follows = listOperations.size(folkey);
        Long fans = listOperations.size(fanKey);
        return new UserActive(userId,fans,follows);
    }

    @Override
    public void follow(UserActiveDTO userActiveDTO) {
        String folkey = "user:"+userActiveDTO.getUserId()+"follow";
        String fankey = "user:"+userActiveDTO.getToUserId()+"fan";
        ListOperations listOperations = redisCache.redisTemplate.opsForList();

        //发起关注的对象
        FolFanDTO fan = new FolFanDTO(userActiveDTO.getUserId(),userActiveDTO.getUsername(),userActiveDTO.getAvatar());
        //被关注的对象
        FolFanDTO fol = new FolFanDTO(userActiveDTO.getToUserId(),userActiveDTO.getToUserName(),userActiveDTO.getToAvatar());

        //将被关注对象加入用户关注列表
        listOperations.leftPush(folkey,fol);
        //将发起关注对象 加入 被关注对象的粉丝列表
        listOperations.leftPush(fankey,fan);
    }

    @Override
    public PageResult pageQueryFolOrFan(int folOrFan, FolFanPageDTO folFanPageDTO) {
        // 起始位置和末尾位置
        int pageNum = folFanPageDTO.getPageNum();
        int pageSize = folFanPageDTO.getPageSize();
        Long userId = folFanPageDTO.getUserId();
        int start = (pageNum-1) * pageSize;
        int end = start + pageSize;

        ListOperations listOperations = redisCache.redisTemplate.opsForList();
        // 关注列表或者粉丝列表
        String key = folOrFan == 0 ? "user:"+userId+"follow" : "user:"+userId+"fan";
        List<FolFanDTO> folFanDTOS = listOperations.range(key, start, end);
        Long total = listOperations.size(key);
        return new PageResult(total,folFanDTOS);
    }
}
