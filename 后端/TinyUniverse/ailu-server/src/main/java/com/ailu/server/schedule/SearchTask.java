package com.ailu.server.schedule;

import com.ailu.dto.article.ArticleTextDTO;
import com.ailu.server.config.RedisCache;
import com.ailu.server.mapper.ArticleMapper;
import com.ailu.util.DocUtils;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/4 下午11:44
 */

@Component
public class SearchTask {

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ArticleMapper articleMapper;

    //TODO:基于分词+倒排索引存储到redis
    @XxlJob("tokenizedInvertedInRedis")
    public void tokenizedInvertedInRedis(){
        //先删除原本的缓存
        redisCache.deleteObject("article_word");
        //获取全部文章
        List<ArticleTextDTO> articleTextDTOs = articleMapper.getArticles();
        Map<String,List<Long>> map = new HashMap<>();
        for (ArticleTextDTO articleTextDTO : articleTextDTOs) {
            String title = articleTextDTO.getTitle();
            String content = articleTextDTO.getContent();
            String author = articleTextDTO.getAuthor();
            Long id = articleTextDTO.getId();

            //去除content的html标签
            content = DocUtils.getText(content);

            //对以上内容进行分词
            JiebaSegmenter jiebaSegmenter = new JiebaSegmenter();
            List<String> words = jiebaSegmenter.sentenceProcess(title + content + author);
            for (String word : words) {
                List<Long> ids = map.computeIfAbsent(word, k -> new ArrayList<>());
                ids.add(id);
            }
        }
        redisCache.setCacheMap("article_word",map);
    }
}
