package com.ailu.server.service.impl.user;

import com.ailu.context.BaseContext;
import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.FolFanPageDTO;
import com.ailu.dto.user.UserActiveDTO;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;
import com.ailu.server.config.RedisCache;
import com.ailu.server.service.user.UserActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

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
        // String key = "user_active:userId:" + userId;
        // Map<String, Object> userActive = redisCache.getCacheMap(key);
        // if(userActive == null){
        //     userActive = new HashMap<>();
        //     redisCache.setCacheMap(key,userActive);
        // }
        String folkey = "user:"+userId+"follow";
        String fankey = "user:"+userId+"fan";
        // BoundZSetOperations<String, FolFanDTO> boundFolOps = redisCache.redisTemplate.boundZSetOps(folkey);
        // BoundZSetOperations<String, FolFanDTO> boundFanOps = redisCache.redisTemplate.boundZSetOps(fankey);
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        Long follows = setOperations.size(folkey);
        Long fans = setOperations.size(fankey);
        return new UserActive(userId,fans,follows);
    }

    @Override
    public void follow(Long toUserId,Boolean isFollow) {
        String folkey = "user:" + BaseContext.getCurrentId() + "follow";
        String fankey = "user:" + toUserId + "fan";

        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        // BoundZSetOperations<String, FolFanDTO> boundFolOps = redisCache.redisTemplate.boundZSetOps(folkey);
        // BoundZSetOperations<String, FolFanDTO> boundFanOps = redisCache.redisTemplate.boundZSetOps(fankey);

        // 发起关注的对象
        // FolFanDTO fan = new FolFanDTO(userActiveDTO.getUserId(), userActiveDTO.getUsername(), userActiveDTO.getAvatar());
        // 被关注的对象
        // FolFanDTO fol = new FolFanDTO(userActiveDTO.getToUserId(), userActiveDTO.getToUserName(), userActiveDTO.getToAvatar());

        // 关注
        if (isFollow) {
            // 将被关注对象加入用户关注列表
            // boundFolOps.add(fol, System.currentTimeMillis());
            setOperations.add(folkey,toUserId);
            // 将发起关注对象 加入 被关注对象的粉丝列表
            // boundFanOps.add(fan, System.currentTimeMillis());
            setOperations.add(fankey,toUserId);
        }
        // 取消关注
        else {
            // 将被关注对象从用户关注列表中移除
            setOperations.remove(folkey,toUserId);
            // 将发起关注对象从被关注对象的粉丝列表中移除
            setOperations.remove(fankey,toUserId);
        }
    }

    @Override
    public PageResult pageQueryFolOrFan(int folOrFan, FolFanPageDTO folFanPageDTO) {
        // 起始位置和末尾位置
        int pageNum = folFanPageDTO.getPageNum();
        int pageSize = folFanPageDTO.getPageSize();
        Long userId = folFanPageDTO.getUserId();
        int start = (pageNum-1) * pageSize;
        int end = start + pageSize - 1;

        // 关注列表或者粉丝列表
        String key = folOrFan == 0 ? "user:"+userId+"follow" : "user:"+userId+"fan";

        BoundZSetOperations<String, FolFanDTO> boundOps = redisCache.redisTemplate.boundZSetOps(key);

        //因为是获取最新时间戳的，所以分数越高优先级越高
        //range为闭区间
        Set<FolFanDTO> folFanDTOS = boundOps.reverseRange(start, end);
        Long total = boundOps.size();
        return new PageResult(total,folFanDTOS);
    }

    @Override
    public Boolean isFollow(Long followId) {
        String folkey = "user:" + BaseContext.getCurrentId() + "follow";
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        return !setOperations.isMember(folkey,followId);
    }
}
