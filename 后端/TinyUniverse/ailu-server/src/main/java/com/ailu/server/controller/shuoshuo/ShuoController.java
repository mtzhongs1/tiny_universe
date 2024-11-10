package com.ailu.server.controller.shuoshuo;

import com.ailu.aop.Log;
import com.ailu.aop.OperationType;
import com.ailu.dto.shuo.ShuoDTO;
import com.ailu.entity.Shuo;
import com.ailu.result.Result;
import com.ailu.server.service.shuoshuo.ShuoService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/11 下午1:30
 */

@RestController
@RequestMapping("/shuo")
public class ShuoController {
    @Autowired
    private ShuoService shuoService;

    @GetMapping("/page")
    public Result<Page<Shuo>> pageQueryShuo(@RequestParam Integer pageSize,
                                            @RequestParam Integer pageNum,
                                            @RequestParam Integer type) {
        Page<Shuo> shuo = shuoService.pageQueryShuo(pageSize,pageNum,type);
        return Result.success(shuo);
    }


    @PostMapping
    @Log(title = "说说系统",operationType = OperationType.INSERT)
    public Result saveShuo(ShuoDTO shuoDTO){
        shuoService.saveShuo(shuoDTO);
        return Result.success();
    }

    @DeleteMapping
    @Log(title = "说说系统",operationType = OperationType.DELETE)
    public Result deleteShuo(Long id){
        shuoService.deleteShuo(id);
        return Result.success();
    }

    @PutMapping
    @Log(title = "说说系统",operationType = OperationType.UPDATE)
    public Result updateShuo(ShuoDTO shuoDTO){
        shuoService.updateShuo(shuoDTO);
        return Result.success();
    }



}
