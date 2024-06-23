package com.ailu.controller.article;

import com.ailu.entity.Tag;
import com.ailu.result.Result;
import com.ailu.service.TagService;
import com.ailu.vo.article.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 16/6/2024 下午8:52
 */

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    @Cacheable(value = "tags", key = "#name")
    public Result<List<TagVO>> getTags(String name){
        List<TagVO> tags = tagService.getTags(name);
        return Result.success(tags);
    }
}
