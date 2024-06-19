package com.ailu.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class User {

  private Long id;
  private String username;
  private String password;
  private LocalDate birthday;
  private Integer sex;
  private String avatar;
  private String email;
  private String description;
  private Boolean status;
  private LocalDateTime createTime;
  private LocalDateTime updateTime;


}
