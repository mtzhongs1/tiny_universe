package com.ailu.server.controller.common;

import com.ailu.result.Result;
import com.ailu.server.service.common.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/18 下午11:15
 */

@RestController
@RequestMapping("/setting")
public class SettingController {
    @Autowired
    private SettingService settingService;
    @GetMapping("/background")
    public Result getImages() {
        String images = settingService.getImages();
        return Result.success(images);
    }
    @PostMapping("/background")
    public Result updateImages(@RequestParam("image") String url, @RequestParam("index") int index){
        settingService.updateImages(url, index);
        return Result.success();
    }

}
