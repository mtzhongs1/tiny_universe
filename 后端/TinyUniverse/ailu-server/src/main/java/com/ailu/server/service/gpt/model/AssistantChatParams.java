package com.ailu.server.service.gpt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssistantChatParams {
    private String messageId;
    private String systemMessage;
    private String userMessage;
}
