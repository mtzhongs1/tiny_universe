package com.ailu.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/12 下午9:16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleScoreDTO implements Serializable, Comparable<ArticleScoreDTO> {

    private Long id;
    private Long score;
    private Long love;
    private Long commentCount;
    private Long collectionCount;
    private Long watch;

    //TODO:降序
    @Override
    public int compareTo(ArticleScoreDTO o) {
        return o.score.compareTo(this.score);
    }

    public void calculateScore() {
        this.score = this.watch + this.love*2 + this.commentCount*3 + this.collectionCount*4;
    }

    public void setField(String type,Object value){
        switch (type){
            case "love":
                this.love = (Long) value;
                break;
            case "commentCount":
                this.commentCount = (Long) value;
                break;
            case "collectionCount":
                this.collectionCount = (Long) value;
                break;
            case "watch":
                this.watch = (Long) value;
                break;
       }
    }
}
