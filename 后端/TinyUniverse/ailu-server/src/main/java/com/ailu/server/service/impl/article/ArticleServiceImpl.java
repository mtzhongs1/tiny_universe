package com.ailu.server.service.impl.article;

import com.ailu.context.BaseContext;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.article.ArticlePageDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.entity.ArticleActive;
import com.ailu.entity.UserActive;
import com.ailu.exception.BaseException;
import com.ailu.result.PageResult;
import com.ailu.server.config.RedisCache;
import com.ailu.server.controller.article.TagController;
import com.ailu.server.mapper.ArticleActiveMapper;
import com.ailu.server.mapper.ArticleMapper;
import com.ailu.server.mapper.ArticleTagMapper;
import com.ailu.server.mapper.TagMapper;
import com.ailu.server.service.article.ArticleService;
import com.ailu.server.service.article.TagService;
import com.ailu.server.service.comment.CommentService;
import com.ailu.server.service.common.MinioService;
import com.ailu.server.service.user.UserActiveService;
import com.ailu.util.DocUtils;
import com.ailu.vo.article.ArticleAndActiveVO;
import com.ailu.vo.article.ArticleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xxl.job.core.executor.XxlJobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    @Autowired
    private UserActiveService userActiveService;

    @Autowired
    private CommentService commentService;
    @Autowired
    private TagController tagController;

    @Override
    @Transactional
    public void publishArticle(Article article) {
        Long userId = BaseContext.getCurrentId();
        article.setUserId(userId);
        // 添加文章
        articleMapper.saveArticle(article);
        Long articleId = article.getId();
        String[] tags = article.getTag().split(",");
        List<Integer> tagIds = Arrays.stream(tags).map(Integer::parseInt).collect(Collectors.toList());
        tagMapper.addTagCount(tagIds);
        // 修改article_tag
        articleTagMapper.saveArticleTag(articleId,tagIds);
        //将article的社交属性存到Redis中

        // ArticleActive articleActive = new ArticleActive(articleId, 0L, 0L, 0L,0L);
        // redisCache.setCacheObject("article_active:"+articleId, articleActive);
        // deleteArticlePageCache();
        setArticleActiveHash(articleId);
    }

    @Override
    public PageResult pageQueryArticle(ArticlePageDTO articlePageDTO) {
        PageHelper.startPage(articlePageDTO.getPageNum(),articlePageDTO.getPageSize());
        // 分页结果
        Page<ArticleAndActiveVO> page = articleMapper.pageQueryArticle(articlePageDTO);
        List<ArticleAndActiveVO> articles = page.getResult();
        Long total = page.getTotal();

        filterArticleByTag(articles,articlePageDTO.getTag());
        setArticleActive(articles);

        return new PageResult(total,articles);
    }

    private void filterArticleByTag(List<ArticleAndActiveVO> articles, Integer tag) {
        if(tag != null && tag != -1){
            Set<Long> articleIdById = tagService.getArticleIdById(tag);
            articles.removeIf(article -> !articleIdById.contains(article.getId()));
        }
    }

    @Override
    public UserActiveVO getUser(Long articleId) {
        UserActiveVO userActiveVO = articleMapper.getUserIdByArticleId(articleId);
        UserActive userActive = userActiveService.getUserActive(userActiveVO.getUserId());
        userActiveVO.setFans(userActive.getFans());
        userActiveVO.setFollows(userActive.getFollows());
        return userActiveVO;
    }

    @Override
    public ArticleVO getArticle(Long articleId) {
        ArticleVO articleVO = articleMapper.getArticle(articleId);
        List<String> tagNames = tagService.getTagNames(articleId);
        articleVO.setTag(tagNames);
        return articleVO;
    }

    @Override
    @Transactional
    public void deleteArticle(List<Long> ids) {
        //删除文章缓存
        // deleteArticlePageCache();
        //删除文章活动属性缓存
        deleteArticleActiveCache(ids);
        articleMapper.deleteArtOrDra(ids,1);
        //删除文章评论
        commentService.deleteCommentByArticleId(ids);

    }

    @Override
    public void updateArticle(ArticleDTO articleDTO) {
        // deleteArticlePageCache();
        articleMapper.updateArticle(articleDTO);
    }

    @Override
    public PageResult search(String name, int pageNum, int pageSize,int type) {
        List<Long> articleIds = redisCache.getCacheMapValue("article_word", name);
        PageHelper.startPage(pageNum, pageSize);
        if(articleIds.isEmpty()){
            return new PageResult(0L,null);
        }
        Page<ArticleAndActiveVO> page = articleMapper.getArticlesByIds(articleIds,type);
        List<ArticleAndActiveVO> articles = page.getResult();
        for (ArticleAndActiveVO article : articles) {
            String content = article.getContent();
            String text = DocUtils.getText(content);
            int index = text.indexOf(name);
            //content作为显示内容
            if (index != -1) {
                //选择前20到后20的字符串
                int start = Math.max(0,index - 20);
                int end = Math.min(index + name.length() + 20,text.length());
                String substring = text.substring(start, end);
                article.setContent(substring);
            }else if(StringUtils.isNotEmpty(article.getDescription())){
                //选择描述
                article.setContent(article.getDescription());
                //因为是通过判断描述是否为空来选择是否要显示文章内容，所以这里要设置description为空
                article.setDescription(null);
            }else{
                //选择前40个字符
                article.setContent(content.substring(0,Math.min(40,content.length())));
            }
        }
        setArticleActive(articles);

        return new PageResult(page.getTotal(),articles);
    }

    private void setArticleActive(List<ArticleAndActiveVO> articles) {
        Long userId = BaseContext.getCurrentId();
        //活动属性的key
        List<String> articleIds = articles.stream().map(article -> "article_active:" + article.getId()).collect(Collectors.toList());
        // Redis批量查询
        //TODO：错误做法，redisCache.redisTemplate.opsForHash().entries(articleId)得到的是Map<R,T>,而collect得到的是不明确的类型，则类型推断失败
        //List<Map<String,Long>> articleActives = articleIds.stream().map(articleId -> {
        //     return redisCache.redisTemplate.opsForHash().entries(articleId);
        //}).collect(Collectors.toList());

        //活动属性数据
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
        //将活动属性设置到文章中
        for (ArticleAndActiveVO article : articles) {

            Long articleId = article.getId();
            Map<String,Long> articleActiveMap = articleActiveMaps.get(articleId);
            if(articleActiveMap == null){
                articleActiveMap = setArticleActiveHash(articleId);
            }
            article.setContent(DocUtils.getText(article.getContent()));
            article.setCollectionCount(articleActiveMap.get("collectionCount"));
            article.setWatch(articleActiveMap.get("watch"));
            article.setCommentCount(articleActiveMap.get("commentCount"));
            article.setLove(articleActiveMap.get("love"));

            //这里不能直接拿userId,因为userId可能是别人的也可能是当前用户的，而现在是要为了确认当前用户有没有点赞，所以要用BaseContext.getCurrentId();
            article.setIsLove(setOperations.isMember("article_active:love:" + article.getId(),userId));
            article.setIsCollection(setOperations.isMember("article_active:collection:" + article.getId(),userId));
        }
    }


    private static String getKey(ArticlePageDTO articlePageDTO) {
        Long userId = articlePageDTO.getUserId();
        Integer tag = articlePageDTO.getTag();
        return String.format("query_article_userId:%d:pageNum:%d:pageSize:%d:type:%d:tag:%d",
                userId == null ? -1 : userId, articlePageDTO.getPageNum(),
                articlePageDTO.getPageSize(), articlePageDTO.getType(),
                tag == null ? -1 : tag);
    }
    private void deleteArticlePageCache() {
        Long userId = BaseContext.getCurrentId();
        //删除查询缓存
        String key1 = "query_article_userId:" + userId;
        String key2 = "query_article_userId:all";
        redisCache.deleteObject(redisCache.redisTemplate.keys(key1+"*"));
        redisCache.deleteObject(redisCache.redisTemplate.keys(key2 + "*"));
    }


    private void deleteArticleActiveCache(List<Long> ids) {
        //删除查询缓存
        String key = "article_active:";
        List<String> actKeys = ids.stream().map(id -> key + id).collect(Collectors.toList());
        redisCache.deleteObject(actKeys);
    }

    private Map<String, Long> setArticleActiveHash(Long articleId) {
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
        return articleActiveMap;
    }

}
