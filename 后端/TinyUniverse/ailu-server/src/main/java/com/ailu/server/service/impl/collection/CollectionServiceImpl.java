package com.ailu.server.service.impl.collection;

import com.ailu.context.BaseContext;
import com.ailu.dto.collection.CollectionDTO;
import com.ailu.entity.Collections;
import com.ailu.exception.BaseException;
import com.ailu.result.PageResult;
import com.ailu.server.mapper.CollectionMapper;
import com.ailu.server.service.collection.CollectionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public PageResult collections(int pageSize, int pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Collections> page = collectionMapper.getAllCollections(BaseContext.getCurrentId());
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public PageResult collection(int pageSize, int pageNum, Long parentId) {
        PageHelper.startPage(pageNum,pageSize);
        Page<Collections> page = collectionMapper.getAllCollection(parentId,BaseContext.getCurrentId());
        return new PageResult(page.getTotal(),page.getResult());
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
