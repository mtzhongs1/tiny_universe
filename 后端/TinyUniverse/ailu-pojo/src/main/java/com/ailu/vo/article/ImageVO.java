package com.ailu.vo.article;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author: ailu
 * @Date: 15/6/2024 下午2:12
 */

@Data
@NoArgsConstructor
public class ImageVO {
    private String url;
    private String alt;
    private String href;

    public ImageVO(String url) {
        this.url = url;
    }
}
