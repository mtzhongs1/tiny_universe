package com.ailu.server.controller.article;

import com.ailu.result.Result;
import com.ailu.server.service.article.TagService;
import com.ailu.vo.article.TagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public Result<List<TagVO>> getTags(@Value("") String name){
        List<TagVO> tags = tagService.getTags(name);
        return Result.success(tags);
    }
}
