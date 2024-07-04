package com.ailu.controller.user;

import com.ailu.dto.user.MessageDTO;
import com.ailu.entity.Message;
import com.ailu.result.Result;
import com.ailu.service.user.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2024/7/3 下午2:41
 */

@RestController
@RequestMapping(value = "/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping
    public Result<List<Message>> getHistory(){
        List<Message> messages = chatService.getHistory();
        return Result.success(messages);
    }

    @PostMapping
    public Result saveHistory (@RequestBody List<MessageDTO> messageDTOs){
        chatService.saveHistory(messageDTOs);
        return Result.success();
    }
}
