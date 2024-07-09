package com.ailu.server.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.entity.ArticleActive;
import com.ailu.server.config.RedisCache;
import com.ailu.server.service.article.ArticleActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        //收藏功能
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

    @Override
    public ArticleActive getArticleActive(Long articleId) {
        Map<String, Long> cacheMap = redisCache.getCacheMap("article_active:" + articleId);
        ArticleActive articleActive = new ArticleActive(articleId, cacheMap.get("love"),
                cacheMap.get("commentCount"), cacheMap.get("collectionCount"),
                cacheMap.get("watch"));
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        //这里不能直接拿userId,因为userId可能是别人的也可能是当前用户的，而现在是要为了确认当前用户有没有点赞，所以要用BaseContext.getCurrentId();
        if(Boolean.TRUE.equals(setOperations.isMember("article_active:love:" + articleId, BaseContext.getCurrentId()))){
            articleActive.setIsLove(true);
        }else{
            articleActive.setIsLove(false);
        }
        if(Boolean.TRUE.equals(setOperations.isMember("article_active:collection:" + articleId, BaseContext.getCurrentId()))){
            articleActive.setIsCollection(true);
        }else{
            articleActive.setIsCollection(false);
        }
        return articleActive;
    }
}
