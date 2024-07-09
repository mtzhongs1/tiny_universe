package com.ailu.server.service.impl.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.entity.Message;
import com.ailu.server.mapper.ChatMapper;
import com.ailu.server.service.user.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午2:43
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;

    @Override
    public List<Message> getHistory() {
        List<Message> messages = chatMapper.getHistory();
        return messages;
    }

    @Override
    public void saveHistory(List<MessageDTO> messageDTOs) {
        chatMapper.saveHistory(messageDTOs);
    }
}
