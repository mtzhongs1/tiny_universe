package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/8 上午10:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogEntity {

    private Long id;

    private Long userId;
    private Integer status;
    private String ip;
    private String url;
    private String errorMsg;

    private String method;
    private String requestMethod;
    private Long costTime;
    private String location;

    private Integer operationType;
    private String title;
    private Integer OperatorType;
    private String jsonResult;

    private String operParam;
    private LocalDateTime operTime;
}
