package com.ailu.server.service.collection;

import com.ailu.dto.collection.CollectionDTO;
import com.ailu.result.PageResult;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/23 下午8:49
 */

public interface CollectionService {
    PageResult collections(int pageSize,int pageNum);

    void saveCollections(CollectionDTO collectionDTO);

    void saveCollection(CollectionDTO collectionDTO);

    PageResult collection(int pageSize, int pageNum, Long parentId);

    void deleteCollection(Long id);

    void deleteCollections(Long id);

    void updateCollection(CollectionDTO collectionDTO);
}
