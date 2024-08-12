package com.ailu.server.controller.collection;

import com.ailu.dto.collection.CollectionDTO;
import com.ailu.result.PageResult;
import com.ailu.result.Result;
import com.ailu.server.service.collection.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/23 下午7:30
 */

@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;
    @GetMapping("/collections/{pageSize}/{pageNum}")
    public Result<PageResult> collections(@PathVariable int pageSize, @PathVariable int pageNum) {
        PageResult collections =  collectionService.collections(pageSize, pageNum);
        return Result.success(collections);
    }
    @GetMapping("/collection/{pageSize}/{pageNum}/{parentId}")
    public Result<PageResult> collection(@PathVariable int pageSize, @PathVariable int pageNum, @PathVariable Long parentId) {
        PageResult collection =  collectionService.collection(pageSize, pageNum,parentId);
        return Result.success(collection);
    }
    @PostMapping("/collections")
    public Result saveCollections(@RequestBody CollectionDTO collectionsDTO) {
        collectionService.saveCollections(collectionsDTO);
        return Result.success();
    }
    @PostMapping("/collection")
    public Result saveCollection(@RequestBody CollectionDTO collectionDTO) {
        collectionService.saveCollection(collectionDTO);
        return Result.success();
    }
    @DeleteMapping("/collections/{id}")
    public Result deleteCollections(@PathVariable Long id) {
        collectionService.deleteCollections(id);
        return Result.success();
    }

    @DeleteMapping("/collection/{id}")
    public Result deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return Result.success();
    }

    @PutMapping
    public Result updateCollection(@RequestBody CollectionDTO collectionDTO) {
        collectionService.updateCollection(collectionDTO);
        return Result.success();
    }
}
