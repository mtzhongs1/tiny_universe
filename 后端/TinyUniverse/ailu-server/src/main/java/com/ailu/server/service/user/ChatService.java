package com.ailu.server.service.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.entity.Message;

import java.util.List;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/3 下午2:43
 */

public interface ChatService {
    List<Message> getHistory();

    void saveHistory(List<MessageDTO> messageDTO);

}
