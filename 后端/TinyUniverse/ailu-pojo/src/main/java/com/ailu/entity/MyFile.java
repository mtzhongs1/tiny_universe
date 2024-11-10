package com.ailu.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/10/14 下午1:23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyFile {
    private Long id;
    private Long userId;
    private String uuid;
    private String path;
    private String ext;

}
