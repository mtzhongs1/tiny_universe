package com.ailu.manage.service.impl;

import com.ailu.entity.LogEntity;
import com.ailu.manage.mapper.LogMapper;
import com.ailu.manage.service.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/8 下午12:47
 */

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;

}
