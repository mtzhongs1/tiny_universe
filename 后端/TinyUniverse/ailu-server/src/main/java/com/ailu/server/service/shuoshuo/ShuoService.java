package com.ailu.server.service.shuoshuo;

import com.ailu.dto.shuo.ShuoDTO;
import com.ailu.entity.Shuo;
import com.github.pagehelper.Page;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/11 下午1:37
 */

public interface ShuoService {
    Page<Shuo> pageQueryShuo(Integer pageSize, Integer pageNum,Integer type);

    Shuo getShuo(Long id);

    void saveShuo(ShuoDTO shuoDTO);

    void deleteShuo(Long id);

    void updateShuo(ShuoDTO shuoDTO);

}
