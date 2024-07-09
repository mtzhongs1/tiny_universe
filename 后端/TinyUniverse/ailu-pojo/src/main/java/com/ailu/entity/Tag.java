package com.ailu.entity;


import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Tag {

  private Long id;
  private String name;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;
  private Long count;

}
