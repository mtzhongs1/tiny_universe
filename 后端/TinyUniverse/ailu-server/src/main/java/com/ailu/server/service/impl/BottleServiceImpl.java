package com.ailu.server.service.impl;

import com.ailu.context.BaseContext;
import com.ailu.entity.Bottle;
import com.ailu.server.mapper.BottleMapper;
import com.ailu.server.service.bottle.BottleService;
import com.ailu.server.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/2 下午1:40
 */

@Service
public class BottleServiceImpl implements BottleService {

    @Autowired
    private BottleMapper bottleMapper;
    @Autowired
    private RedisCache redisCache;
    @Override
    public Bottle getBottle() {
        List<Long> bottleIds = redisCache.getCacheList("bottle:"+BaseContext.getCurrentId());
        Bottle bottle = bottleMapper.getBottle(bottleIds,BaseContext.getCurrentId());
        if(bottle != null){
            if(bottle.getIsPublic() == 0){
                bottle.setUsername("匿名用户");
            }
            bottleIds.add(bottle.getId());
            redisCache.setCacheList("bottle:"+BaseContext.getCurrentId(), bottleIds);
        }
        return bottle;
    }

    @Override
    public void saveBottle(Bottle bottle) {
        bottleMapper.saveBottle(bottle);
    }

    @Override
    public List<Bottle> getBottles(Integer type) {
        List<Bottle> bottles = new ArrayList<>();
        //查询最新
        if(type == 0){
            List<Long> bottleIds = redisCache.getCacheList("bottle:"+BaseContext.getCurrentId());
            if(bottleIds.size() > 0){
                bottles = bottleMapper.getBottles(type,bottleIds);
            }
        }
        //查询抛出的
        else{
            bottles = bottleMapper.getBottlesByUserId(BaseContext.getCurrentId());
        }

        for (Bottle bottle : bottles) {
            if(bottle.getIsPublic() == 0){
                bottle.setUsername("匿名用户");
            }
        }
        return bottles;
    }

    @Override
    public List<Bottle> getBottles(Long parentId) {
        return bottleMapper.getBottlesByParentId(parentId);
    }
}
