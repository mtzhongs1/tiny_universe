package com.ailu.server.service.impl.user;

import com.ailu.context.BaseContext;
import com.ailu.dto.user.FolFanDTO;
import com.ailu.dto.user.FolFanPageDTO;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;
import com.ailu.server.config.RedisCache;
import com.ailu.server.mapper.UserMapper;
import com.ailu.server.service.user.UserActiveService;
import com.ailu.vo.user.UserSocketVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
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

    @Autowired
    private UserMapper userMapper;

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
        ZSetOperations zSetOperations = redisCache.redisTemplate.opsForZSet();
        Long follows = zSetOperations.size(folkey);
        Long fans = zSetOperations.size(fankey);
        return new UserActive(userId,fans,follows);
    }

    @Override
    public void follow(Long toUserId,Boolean isFollow) {
        Long userId = BaseContext.getCurrentId();
        String folkey = "user:" + userId + "follow";
        String fankey = "user:" + toUserId + "fan";
        // BoundZSetOperations<String, FolFanDTO> boundFolOps = redisCache.redisTemplate.boundZSetOps(folkey);
        // BoundZSetOperations<String, FolFanDTO> boundFanOps = redisCache.redisTemplate.boundZSetOps(fankey);

        // 发起关注的对象
        // FolFanDTO fan = new FolFanDTO(userActiveDTO.getUserId(), userActiveDTO.getUsername(), userActiveDTO.getAvatar());
        // 被关注的对象
        // FolFanDTO fol = new FolFanDTO(userActiveDTO.getToUserId(), userActiveDTO.getToUserName(), userActiveDTO.getToAvatar());

        // 关注
        // TODO: 依据redis的管道机制批量实现redis操作
        redisCache.redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();
                if (isFollow) {
                    // 将被关注对象加入用户关注列表
                    // boundFolOps.add(fol, System.currentTimeMillis());
                    operations.opsForZSet().add(folkey,toUserId,System.currentTimeMillis());
                    // 将发起关注对象 加入 被关注对象的粉丝列表
                    // boundFanOps.add(fan, System.currentTimeMillis());
                    operations.opsForZSet().add(fankey,userId,System.currentTimeMillis());
                }
                // 取消关注
                else {
                    // 将被关注对象从用户关注列表中移除
                    operations.opsForZSet().remove(folkey,toUserId);
                    // 将发起关注对象从被关注对象的粉丝列表中移除
                    operations.opsForZSet().remove(fankey,userId);
                }
                return operations.exec();
            }
        });
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
        String key = (folOrFan == 1) ? "user:"+userId+"follow" : "user:"+userId+"fan";

        BoundZSetOperations<String, Long> boundOps = redisCache.redisTemplate.boundZSetOps(key);

        //因为是获取最新时间戳的，所以分数越高优先级越高
        //range为闭区间
        Set<Long> ids = boundOps.reverseRange(start, end);
        long total = boundOps.size();
        List<FolFanDTO> folFanDTOS = null;
        if (ids != null && !ids.isEmpty()) {
            folFanDTOS = userMapper.getUsersByFid(ids);
        }
        folFanDTOS.forEach(folFanDTO -> {folFanDTO.setIsFollow(isFollow(folFanDTO.getId()));});
        return new PageResult(total,folFanDTOS);
    }

    @Override
    public Boolean isFollow(Long followId) {
        String folkey = "user:" + BaseContext.getCurrentId() + "follow";
        ZSetOperations setOperations = redisCache.redisTemplate.opsForZSet();
        Double score = setOperations.score(folkey, followId);
        return score == null;
    }
}
