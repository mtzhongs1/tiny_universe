package com.ailu.service.impl.common;

import com.ailu.context.BaseContext;
import com.ailu.mapper.SettingMapper;
import com.ailu.service.common.SettingService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/6/18 下午11:18
 */
@Service
public class SettingServiceImpl implements SettingService {

    @Autowired
    private SettingMapper settingMapper;

    @Override
    public String getImages() {
        return settingMapper.getImages(BaseContext.getCurrentId());
    }

    @Override
    public void updateImages(String url, int index) {
        // 根据index索引值，修改setting表的背景图片url的第index个逗号后面的参数
        // 例如：url = "a,b,c,d,e" index = 2 修改后为 "a,b,update,d,e"
        String images = settingMapper.getImages(BaseContext.getCurrentId());
        String[] imageArray = images.split(",");
        imageArray[index] = url;
        images = StringUtils.join(imageArray, ",");
        settingMapper.updateSetting(BaseContext.getCurrentId(), images);
    }


}
