package com.ailu.server.service.impl.article;

import com.ailu.constant.ArticleScoreConstant;
import com.ailu.context.BaseContext;
import com.ailu.dto.article.ArticleDTO;
import com.ailu.dto.article.ArticlePageDTO;
import com.ailu.dto.article.ArticleScoreDTO;
import com.ailu.dto.user.UserActiveVO;
import com.ailu.entity.Article;
import com.ailu.entity.UserActive;
import com.ailu.result.PageResult;
import com.ailu.server.util.RedisCache;
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
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResult pageQueryArticle(ArticlePageDTO articlePageDTO) throws TasteException {
        Page<ArticleAndActiveVO> page = null;
        //推荐
        if(articlePageDTO.getType() == 2){
            page = recommendArticle(articlePageDTO);
            // filterArticleByTag(page.getResult(),articlePageDTO.getTag(),page);
        }else if(articlePageDTO.getType() == 1){
            //最新
            //TODO:PageHelper和order by一起出现会导致的问题：因为一开始没有选择连接article_tag表进行查询，
            // 而是选择在后面过滤不包含该tag的文章，会导致tag标签中的文章因为分页的关系没有查询到
            PageHelper.startPage(articlePageDTO.getPageNum(),articlePageDTO.getPageSize());
            page = articleMapper.pageQueryArticle(articlePageDTO);

        }else{
            //热门
            List<ArticleScoreDTO> articleScoreDTO = getArticleIds();
            // PageHelper.startPage(articlePageDTO.getPageNum(),articlePageDTO.getPageSize());
            // page = articleMapper.pageQueryArticle(articlePageDTO);
            // int total = articleScoreDTO.size();
            PageHelper.startPage(articlePageDTO.getPageNum(),articlePageDTO.getPageSize());
            page = articleMapper.pageQueryArticleByIds(articleScoreDTO,articlePageDTO.getTag());
        }
        setArticleActive(page.getResult());
        return new PageResult(page.getTotal(),page.getResult());
    }

    private List<ArticleScoreDTO> getArticleIds() {
        Set<String> keys = redisCache.getKeys("article_active:[0-9]*");
        HashOperations<String,String,Long> hashOperations = redisCache.redisTemplate.opsForHash();
        //java键值对
        List<Pair<Long,Map<String,Long>>> articleActives = keys.stream().map((key) -> {
            Map<String, Long> map = hashOperations.entries(key);
            return new Pair<>(Long.parseLong(key.substring(key.lastIndexOf(":")+1)),map);
        }).collect(Collectors.toList());
        List<ArticleScoreDTO> articleScores = new ArrayList<>();
        for (Pair<Long,Map<String, Long>> articleActive : articleActives) {
            ArticleScoreDTO articleScore = new ArticleScoreDTO();
            articleScore.setId(articleActive.getKey());
            articleActive.getValue().forEach(articleScore::setField);
            articleScore.calculateScore();
            articleScores.add(articleScore);
        }
        Collections.sort(articleScores);
        return articleScores;
    }

    /*TODO：推荐文章功能，基于用户相似性的协同过滤，即找出与该用户相似的用户，将其他用户喜欢的推荐给改用户
       如果是基于物品相似性的协同过滤，则是找出用户喜欢的物品，找出与该物品相似的其他物品推荐给用户*/
    private Page<ArticleAndActiveVO> recommendArticle(ArticlePageDTO articlePageDTO) throws TasteException {
        //获取用户行为(点赞，收藏)
        //获取所有文章的点赞情况
        // String sLoveKey = "article_active:love:*";
        // String sCollectionKey = "article_active:collection:*";
        //拿到所有的文章id
        List<Long> ids = articleMapper.getArticleIdsByTag(articlePageDTO.getTag());
        //拼接所有的key
        List<String> sLoveKeys = ids.stream().map(id -> "article_active:love:" + id).collect(Collectors.toList());
        List<String> sCollectionKeys = ids.stream().map(id -> "article_active:collection:" + id).collect(Collectors.toList());
        //拿到所有的value
        Map<Long,Map<Long,Integer>> map = new HashMap<>();

        SetOperations setOperations =  redisCache.redisTemplate.opsForSet();
        for(int i = 0 ; i < ids.size() ; i++){
            Long articleId = ids.get(i);
            Set<Long> love = setOperations.members(sLoveKeys.get(i));
            Set<Long> collection = setOperations.members(sCollectionKeys.get(i));
            for (Long userId : love) {
                setUserPreferences(map,userId, articleId,ArticleScoreConstant.love);
            }
            for (Long userId : collection) {
                setUserPreferences(map,userId, articleId,ArticleScoreConstant.collection);
            }
        }
        //获取数据集
        DataModel dataModel = createDataModel(map);
        //获取推荐的文章Id
        List<Long> recommedIds = getArticleByDataModel(dataModel,articlePageDTO.getPageSize());
        PageHelper.startPage(articlePageDTO.getPageNum(),articlePageDTO.getPageSize());
        if(ObjectUtils.isEmpty(recommedIds)){
            return new Page<>();
        }
        return articleMapper.getArticlesByIds(recommedIds,articlePageDTO.getType());
    }

    //根据数据集推荐文章id
    private List<Long> getArticleByDataModel(DataModel dataModel,Integer pageSize) throws TasteException {
        //获取用户相似程度
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        //获取用户邻居
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(pageSize, similarity, dataModel);
        //构建推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //推荐pageSize个
        List<RecommendedItem> recommendedItems = recommender.recommend(BaseContext.getCurrentId(),pageSize);
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        return itemIds;
    }

    private DataModel createDataModel(Map<Long, Map<Long, Integer>> map) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        for (Map.Entry<Long, Map<Long, Integer>> entry : map.entrySet()) {
            Map<Long, Integer> userPreferences = entry.getValue();
            List<GenericPreference> array = new ArrayList<>();
            Long userId = entry.getKey();
            userPreferences.forEach((articleId, value) -> {
                array.add(new GenericPreference(userId,articleId,value));
            });
            fastByIdMap.put(userId,new GenericUserPreferenceArray(array));
        }
        return new GenericDataModel(fastByIdMap);
    }

    private static void setUserPreferences(Map<Long,Map<Long,Integer>> map,Long userId, Long articleId,Integer value) {
        Map<Long, Integer> userPreferences = map.get(userId);
        if(ObjectUtils.isNotEmpty(userPreferences)){
            //不为null,修改喜好值
            userPreferences.put(articleId, userPreferences.getOrDefault(articleId,0)+value);
        }else{
            //为null,放入初始喜好
            userPreferences = new HashMap<>();
            userPreferences.put(articleId, value);
            map.put(userId,userPreferences);
        }
    }

    private void filterArticleByTag(List<ArticleAndActiveVO> articles, Integer tag,Page<ArticleAndActiveVO> page) {
        if(tag != null && tag != -1){
            //当前tag下的所有文章id
            Set<Long> articleIdById = tagService.getArticleIdById(tag);
            articles.removeIf(article -> !articleIdById.contains(article.getId()));
            //因为要求的是分页条件下的当前的文章id,所以总数就是当前tag下的所有文章id
            page.setTotal(articleIdById.size());
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
        //删除tag的文章
        tagService.removeArticle(ids);

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
        if(ObjectUtils.isEmpty(articleIds)){
            return new PageResult(0L,new ArrayList<>());
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
