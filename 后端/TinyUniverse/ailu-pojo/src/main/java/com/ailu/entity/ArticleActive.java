package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleActive implements Serializable {
  private static final long serialVersionUID = 187799455564L;
  private Long id;
  private Long articleId;
  private Long love;
  private Long commentCount;
  private Long collectionCount;
  private Boolean isLove;
  private Boolean isCollection;

  private Long watch;


  public ArticleActive(Long articleId, Long love, Long commentCount, Long collectionCount, Long watch) {

    this.articleId = articleId;
    this.love = love;
    this.commentCount = commentCount;
    this.collectionCount = collectionCount;
    this.watch = watch;

  }
}
