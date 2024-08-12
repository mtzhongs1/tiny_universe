package com.ailu.server.service.impl.shuoshuo;

import com.ailu.context.BaseContext;
import com.ailu.dto.shuo.ShuoDTO;
import com.ailu.entity.Shuo;
import com.ailu.server.mapper.ShuoMapper;
import com.ailu.server.service.shuoshuo.ShuoService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/11 下午1:37
 */
@Service
public class ShuoServiceImpl implements ShuoService {

    @Autowired
    private ShuoMapper shuoMapper;

    @Override
    public Page<Shuo> pageQueryShuo(Integer pageSize, Integer pageNum,Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Shuo> page = shuoMapper.pageQueryShuo(type);
        return page;
    }

    @Override
    public Shuo getShuo(Long id) {
        Shuo shuo = shuoMapper.getShuo(id);
        return shuo;
    }

    @Override
    public void saveShuo(ShuoDTO shuoDTO) {
        shuoDTO.setUserId(BaseContext.getCurrentId());
        shuoMapper.saveShuo(shuoDTO);
    }

    @Override
    public void deleteShuo(Long id) {
        shuoMapper.deleteShuo(id);
    }

    @Override
    public void updateShuo(ShuoDTO shuoDTO) {
        shuoMapper.updateShuo(shuoDTO);
    }


}
