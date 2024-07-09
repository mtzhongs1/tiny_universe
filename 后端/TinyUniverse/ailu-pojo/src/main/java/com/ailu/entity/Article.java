package com.ailu.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Article implements Serializable {

  private Long id;
  private Long userId;
  private String author;
  private String content;
  private String title;
  private String description;
  private String cover;
  private String tag;
  private Boolean status;
  private Boolean type;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;

}
