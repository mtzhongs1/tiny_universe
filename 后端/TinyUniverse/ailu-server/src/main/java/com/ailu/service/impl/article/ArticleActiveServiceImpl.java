package com.ailu.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.service.article.ArticleActiveService;
import com.ailu.util.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

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
        Long love = redisCache.getCacheMapValue(hkey, "love");
        Long userId = BaseContext.getCurrentId();
        boolean isAdd = setOperations.isMember(skey, userId);
        if(isAdd){
            //减少
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
        // 下面错误： 原因，配置的序列化器将redis在序列化时转为String，因此存入的是String类型，自然没办法加一
        // redisCache.redisTemplate.opsForHash().increment("article_active:"+articleId,"watch",1L);
        String key = "article_active:" + articleId;
        Long watch = redisCache.getCacheMapValue(key, "watch");
        watch++;
        redisCache.setCacheMapValue(key,"watch",watch);
    }

    @Override
    public boolean collection(Long articleId){
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        //点赞功能
        String hkey = "article_active:"+articleId;
        String skey = "article_active:collection:"+articleId;
        Long collectionCount = redisCache.getCacheMapValue(hkey, "collectionCount");
        Long userId = BaseContext.getCurrentId();
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
