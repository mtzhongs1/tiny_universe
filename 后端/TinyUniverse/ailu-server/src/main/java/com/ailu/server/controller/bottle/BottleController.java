package com.ailu.server.controller.bottle;

import com.ailu.entity.Bottle;
import com.ailu.result.Result;
import com.ailu.server.service.bottle.BottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/9/2 下午1:36
 */
@RestController
@RequestMapping(value = "/bottle")
public class BottleController {
    @Autowired
    private BottleService bottleService;

    @GetMapping
    public Result<Bottle> getBottle(){

        Bottle bottle = bottleService.getBottle();
        return Result.success(bottle);
    }
    @PostMapping
    public Result saveBottle(@RequestBody Bottle bottle){
        bottleService.saveBottle(bottle);
        return Result.success();
    }
    @GetMapping("/bottles")
    public Result<List<Bottle>> getBottles(Integer type){
        List<Bottle> bottles = bottleService.getBottles(type);
        return Result.success(bottles);
    }
    @GetMapping("/reply")
    public Result<List<Bottle>> getBottleReply(Long parentId){
        List<Bottle> bottles = bottleService.getBottles(parentId);
        return Result.success(bottles);
    }
}
