package com.ailu.server.service.impl;

import com.ailu.entity.LogEntity;
import com.ailu.server.mapper.LogMapper;
import com.ailu.server.service.Log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/8 下午4:04
 */

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;

    @Override
    public void insertLog(LogEntity logEntity) {
        logMapper.insertLog(logEntity);
    }
}
