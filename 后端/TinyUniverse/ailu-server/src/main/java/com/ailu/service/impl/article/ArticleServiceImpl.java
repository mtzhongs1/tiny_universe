package com.ailu.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.entity.Article;
import com.ailu.exception.BaseException;
import com.ailu.mapper.ArticleActiveMapper;
import com.ailu.mapper.ArticleMapper;
import com.ailu.mapper.ArticleTagMapper;
import com.ailu.mapper.TagMapper;
import com.ailu.result.PageResult;
import com.ailu.service.article.ArticleService;
import com.ailu.service.article.TagService;
import com.ailu.service.common.MinioService;
import com.ailu.util.RedisCache;
import com.ailu.vo.article.ArticleAndActiveVO;
import com.ailu.vo.article.ArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: ailu
 * @Date: 12/6/2024 下午4:20
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Autowired
    private ArticleActiveMapper articleActiveMapper;

    @Autowired
    private MinioService minioService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private TagService tagService;
    @Override
    @Transactional
    public void publishArticle(Article article) {
        Long userId = BaseContext.getCurrentId();
        article.setUserId(userId);
        // 添加文章
        articleMapper.saveArticle(article);
        Long articleId = article.getId();
        String[] tags = article.getTag().split(",");
        tagMapper.addTagCount(tags);
        // 修改article_tag
        articleTagMapper.saveArticleTag(articleId,tags);
        //将article的社交属性存到Redis中

        // ArticleActive articleActive = new ArticleActive(articleId, 0L, 0L, 0L,0L,0L);
        // redisCache.setCacheObject("article_active"+articleId, articleActive);
        deleteArticlePageCache();
        setArticleActiveHash(articleId);
    }

    @Override
    public PageResult pageQueryArticle(Long userId, int pageNum, int pageSize) {
        String key = getKey(userId, pageNum, pageSize);
        List<ArticleAndActiveVO> articles = redisCache.getCacheObject(key);
        Long total = null;
        if(ObjectUtils.isEmpty(articles)){
            PageHelper.startPage(pageNum, pageSize);
            Page<ArticleAndActiveVO> page = articleMapper.pageQueryArticle(userId);
            articles  = page.getResult();
            total = page.getTotal();
            redisCache.setCacheObject(key,articles);
        }
        List<String> articleIds = articles.stream().map(article -> "article_active:" + article.getId()).collect(Collectors.toList());
        // Redis批量查询
        //TODO：错误做法，redisCache.redisTemplate.opsForHash().entries(articleId)得到的是Map<R,T>,而collect得到的是不明确的类型，则类型推断失败
        //List<Map<String,Long>> articleActives = articleIds.stream().map(articleId -> {
        //     return redisCache.redisTemplate.opsForHash().entries(articleId);
        //}).collect(Collectors.toList());
        // 这样修改后collect方法里得到的是就是明确的数据类型
        HashOperations<String,String,Long> hashOperations = redisCache.redisTemplate.opsForHash();
        List<Map<String,Long>> articleActives = articleIds.stream().map(hashOperations::entries).collect(Collectors.toList());
        // List<Map<String,Long>> articleActives = articleActiveJsons.stream().map(articleJson -> articleJson.toJavaObject(Map.class)).collect(Collectors.toList());

        // 转为Map
        Map<Long,Map<String,Long>> articleActiveMaps = new HashMap<>();
        for (Map<String, Long> articleActive : articleActives) {
            Long articleId = articleActive.get("articleId");
            articleActiveMaps.put(articleId,articleActive);
        }
        SetOperations setOperations = redisCache.redisTemplate.opsForSet();
        for (ArticleAndActiveVO article : articles) {
            Long articleId = article.getId();
            Map<String,Long> articleActiveMap = articleActiveMaps.get(articleId);
            if(articleActiveMap == null){
                throw new BaseException("服务器繁忙");
            }
            article.setCollectionCount(articleActiveMap.get("collectionCount"));
            article.setWatch(articleActiveMap.get("watch"));
            article.setCommentCount(articleActiveMap.get("commentCount"));
            article.setLove(articleActiveMap.get("love"));

            if(Boolean.TRUE.equals(setOperations.isMember("article_active:love:" + article.getId(), userId.toString()))){
                article.setIsLove(true);
            }else{
                article.setIsLove(false);
            }
            if(Boolean.TRUE.equals(setOperations.isMember("article_active:collection:" + article.getId(), userId.toString()))){
                article.setIsCollection(true);
            }else{
                article.setIsCollection(false);
            }
        }

        return articles;
    }

    @Override
    public ArticleVO getArticle(Long articleId) {
        ArticleVO articleVO = articleMapper.getArticle(articleId);
        List<String> tagNames = tagService.getTagNames(articleId);
        articleVO.setTag(tagNames);
        return articleVO;
    }

    @Override
    public void deleteArticle(List<Long> ids) {
        //删除文章缓存
        deleteArticlePageCache();
        //删除文章活动属性缓存
        deleteArticleActiveCache(ids);
        articleMapper.deleteArtOrDra(ids,1);
    }

    @Override
    public void updateArticle(ArticleDTO articleDTO) {
        deleteArticlePageCache();
        articleMapper.updateArticle(articleDTO);
    }

    private static @NotNull String getKey(Long userId, int pageNum, int pageSize) {
        String key = new StringBuilder().append("query_article_userId:").append(userId)
                .append("pageNum:").append(pageNum).append("pageSize:").append(pageSize).toString();
        return key;
    }
    private void deleteArticlePageCache() {
        Long userId = BaseContext.getCurrentId();
        //删除查询缓存
        String key = "query_article_userId:" + userId;
        redisCache.deleteObject(redisCache.redisTemplate.keys(key+"*"));
    }


    private void deleteArticleActiveCache(List<Long> ids) {
        //删除查询缓存
        String key = "article_active:";
        List<String> actKeys = ids.stream().map(id -> key + id).collect(Collectors.toList());
        redisCache.deleteObject(actKeys);
    }

    private void setArticleActiveHash(Long articleId) {
        String key = "article_active:"+ articleId;
        // redisCache.setCacheMapValue(key,"love",0L);
        // redisCache.setCacheMapValue(key,"forward",0L);
        // redisCache.setCacheMapValue(key,"commentCount",0L);
        // redisCache.setCacheMapValue(key,"watch",0L);
        // redisCache.setCacheMapValue(key,"collectionCount",0L);
        Map<String, Long> articleActiveMap = new HashMap<>();
        articleActiveMap.put("love", 0L);
        articleActiveMap.put("commentCount", 0L);
        articleActiveMap.put("watch", 0L);
        articleActiveMap.put("collectionCount", 0L);
        articleActiveMap.put("articleId",articleId);
        redisCache.setCacheMap(key,articleActiveMap);
    }

}
