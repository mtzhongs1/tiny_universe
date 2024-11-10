package com.ailu.server.service.collection;

import com.ailu.dto.collection.CollectionDTO;
import com.ailu.entity.Collections;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/23 下午8:49
 */

public interface CollectionService {
    List<Collections> collections(Long userId);

    void saveCollections(CollectionDTO collectionDTO);

    void saveCollection(CollectionDTO collectionDTO);

    List<Collections> collection(Long userId, Long parentId);

    void deleteCollection(Long id);

    void deleteCollections(Long id);

    void updateCollection(CollectionDTO collectionDTO);
}
