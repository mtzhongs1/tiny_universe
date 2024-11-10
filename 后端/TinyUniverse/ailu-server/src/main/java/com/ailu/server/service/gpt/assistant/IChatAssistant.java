package com.ailu.server.service.gpt.assistant;

import dev.langchain4j.service.*;

public interface IChatAssistant{

    @SystemMessage("{{sm}}")
    TokenStream chat(@MemoryId String memoryId, @V("sm") String systemMessage, @UserMessage String prompt);

    TokenStream chatWithoutSystemMessage(@MemoryId String memoryId, @UserMessage String prompt);

    @SystemMessage("{{sm}}")
    String nchat(@MemoryId String memoryId, @V("sm") String systemMessage, @UserMessage String prompt);

    String nchatWithoutSystemMessage(@MemoryId String memoryId, @UserMessage String prompt);
}
