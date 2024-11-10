package com.ailu.server.service.impl.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.dto.user.UserSocketPageDTO;
import com.ailu.server.mapper.UserMapper;
import com.ailu.server.service.user.SocketServcie;
import com.ailu.server.socket.EchoChannel;
import com.ailu.server.util.RedisCache;
import com.ailu.vo.user.DataAndCnt;
import com.ailu.vo.user.UserSocketVO;
import com.ailu.vo.user.UserVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 上午10:49
 */
@Service
public class SocketServcieImpl implements SocketServcie {

    @Autowired
    private EchoChannel echoChannel;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void sendAll(MessageDTO message) {

        String messageJson = JSON.toJSONString(message);
        echoChannel.sendToAllClient(messageJson);
    }

    @Override
    public DataAndCnt<UserSocketVO> getUsers(UserSocketPageDTO userSocketPageDTO) {
        String username = userSocketPageDTO.getUsername();

        //随机查询10条数据
        if(StringUtils.isEmpty(username)){
            //TODO: randomEnties不支持windows,等放到云服务再测试
            // Map<String,UserSocketVO> map = redisCache.redisTemplate.opsForHash().randomEntries("chat", userSocketPageDTO.getPageSize());
            Long cnt = redisCache.redisTemplate.opsForHash().size("chat");

            //以下代码是暂时性代码
            Map<String,UserSocketVO> map = redisCache.redisTemplate.opsForHash().entries("chat");
            return new DataAndCnt<>(new ArrayList<>(map.values()),cnt.intValue());
        }else{
            //根据名字模糊查询
            Map<String, JSONObject> map = redisCache.redisTemplate.opsForHash().entries("chat");
            Collection<JSONObject> userSocketVOs = map.values();
            List<UserSocketVO> filters = new ArrayList<>();
            //如果用户名等于username，则移到filters中
            for (JSONObject jsonObject : userSocketVOs) {
                if(jsonObject.getString("username").contains(username)){
                    UserSocketVO userSocketVO = new UserSocketVO(jsonObject.getLong("id"),jsonObject.getString("username"), jsonObject.getString("avatar"));
                    filters.add(userSocketVO);
                }
            }
            return new DataAndCnt<>(filters, userSocketVOs.size());
        }
    }

    @Override
    public void addUser(Long userId) {
        UserVO userVO = userMapper.getUser(userId);
        UserSocketVO userSocketVO = new UserSocketVO();
        BeanUtils.copyProperties(userVO, userSocketVO);
        //添加缓存，key为chat:userId
        redisCache.setCacheMapValue("chat", userId.toString(),userSocketVO);
    }

    @Override
    public void deleteUser(Long userId) {
        //删除缓存
        redisCache.delCacheMapValue("chat", userId.toString());
    }
}
