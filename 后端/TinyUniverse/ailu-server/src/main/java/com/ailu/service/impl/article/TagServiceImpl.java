package com.ailu.service.impl.article;

import com.ailu.mapper.TagMapper;
import com.ailu.service.TagService;
import com.ailu.vo.article.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author: ailu
 * @Date: 16/6/2024 下午8:57
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;
    @Override
    public List<TagVO> getTags(String name) {
        if(Objects.isNull(name)){
            name = "";
        }
        return tagMapper.getTags(name);
    }
}
