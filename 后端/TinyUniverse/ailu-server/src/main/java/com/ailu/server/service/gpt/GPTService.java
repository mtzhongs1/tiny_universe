package com.ailu.server.service.gpt;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/9 下午2:08
 */

public interface GPTService {
    String talk(Long userId,String text);
}
