package com.ailu.server.async;

import com.ailu.entity.LogEntity;
import com.ailu.server.service.Log.LogService;

import com.ailu.util.spring.SpringUtils;


import java.util.TimerTask;

/**
 * 异步工厂（产生任务用）
 *
 * @author ruoyi
 */
public class AsyncFactory
{

    /**
     * 操作日志记录
     *
     * @param logEntity 操作日志信息
     * @return 任务task
     */
    public static TimerTask recordOper(final LogEntity logEntity)
    {
        return new TimerTask()
        {
            @Override
            public void run()
            {
                SpringUtils.getBean(LogService.class).insertLog(logEntity);
            }
        };
    }
}
