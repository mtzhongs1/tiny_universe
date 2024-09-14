package com.ailu.server.dataSource.searchType;

import com.ailu.result.PageResult;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/9/8 下午5:23
 */
// @Service
public interface DataSource {
    PageResult search(String content, Integer pageNum, Integer pageSize, Integer type);
}
