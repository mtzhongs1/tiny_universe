package com.ailu.dto.article;

import com.ailu.entity.Article;
import com.ailu.vo.article.ArticleAndActiveVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/29 下午1:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimplePageDTO implements Serializable{
    private long total; //总记录数

    private List<ArticleAndActiveVO> records; //当前页数据集合
}
