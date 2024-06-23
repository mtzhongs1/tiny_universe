package com.ailu.service.impl;

import com.ailu.context.BaseContext;
import com.ailu.service.ArticleActiveService;
import com.ailu.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/22 上午12:47
 */
@Service
public class ArticleActiveServiceImpl implements ArticleActiveService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public boolean love(Long articleId) {
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        //点赞功能
        String hkey = "article_active:"+articleId;
        String skey = "article_active:love:"+articleId;
        Long love = ((Number)(redisCache.getCacheMapValue(hkey, "love"))).longValue();
        String userId = BaseContext.getCurrentId().toString();
        boolean isAdd = setOperations.isMember(skey, userId);
        if(isAdd){
            //删除
            redisCache.setCacheMapValue(hkey, "love", --love);
            setOperations.remove(skey,userId);
        }else{
            //添加
            redisCache.setCacheMapValue(hkey, "love", ++love);
            setOperations.add(skey,userId);
        }
        return !isAdd;
    }
    @Override
    public void forward() {

    }

    @Override
    public void watch(Long articleId) {

    }

    @Override
    public boolean collection(Long articleId){
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        //点赞功能
        String hkey = "article_active:"+articleId;
        String skey = "article_active:collection:"+articleId;
        Long collectionCount = ((Number)(redisCache.getCacheMapValue(hkey, "collectionCount"))).longValue();
        String userId = BaseContext.getCurrentId().toString();
        boolean isAdd = setOperations.isMember(skey, userId);
        if(isAdd){
            //删除
            redisCache.setCacheMapValue(hkey, "collectionCount", --collectionCount);
            setOperations.remove(skey,userId);
        }else{
            //添加
            redisCache.setCacheMapValue(hkey, "collectionCount", ++collectionCount);
            setOperations.add(skey,userId);
        }
        return !isAdd;
    }
}
