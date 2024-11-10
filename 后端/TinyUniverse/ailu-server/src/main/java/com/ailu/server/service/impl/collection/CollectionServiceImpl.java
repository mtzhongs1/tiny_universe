package com.ailu.server.service.impl.collection;

import com.ailu.dto.collection.CollectionDTO;
import com.ailu.entity.Collections;
import com.ailu.server.mapper.CollectionMapper;
import com.ailu.server.service.collection.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/23 下午8:50
 */
@Service
public class CollectionServiceImpl implements CollectionService {
    @Autowired
    private CollectionMapper collectionMapper;
    @Override
    public List<Collections> collections(Long userId) {
        List<Collections> allCollections = collectionMapper.getAllCollections(userId);
        return allCollections;
    }

    @Override
    public List<Collections> collection(Long userId, Long parentId) {
        List<Collections> allCollection = collectionMapper.getAllCollection(parentId, userId);
        return allCollection;
    }

    @Override
    public void saveCollections(CollectionDTO collectionDTO) {
        collectionDTO.setType(1);
        collectionMapper.saveCollection(collectionDTO);
    }

    @Override
    public void saveCollection(CollectionDTO collectionDTO) {
        collectionDTO.setType(0);
        collectionMapper.saveCollection(collectionDTO);
    }

    @Override
    public void updateCollection(CollectionDTO collectionDTO) {
        collectionMapper.updateCollection(collectionDTO);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionMapper.deleteCollection(id);
    }

    @Override
    public void deleteCollections(Long id) {
        collectionMapper.deleteCollections(id);
    }
}
