package com.ailu.server.service.gpt.store;

import cn.hutool.extra.spring.SpringUtil;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;
import static org.mapdb.Serializer.STRING;

@Slf4j
public class MapDBChatMemoryStore implements ChatMemoryStore {

    public static MapDBChatMemoryStore singleton;

    private final DB db;

    private final Map<String, String> map;

    private MapDBChatMemoryStore() {
        String memoryDir = SpringUtil.getProperty("local.chat-memory");
        log.info("chat memory path:{}", memoryDir);
        db = DBMaker.fileDB(memoryDir + "chat-memory.db").transactionEnable().make();
        map = db.hashMap("messages", STRING, STRING).createOrOpen();
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = map.get((String) memoryId);
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        //AiMessage in first position is not allow
        if (messages.size() > 0 && messages.get(0) instanceof AiMessage) {
            messages.remove(0);
        }
        //Filter out the available messages.(UserMessage,AiMessage)
        List<ChatMessage> availableMessage = new ArrayList<>();
        for (ChatMessage chatMessage : messages) {
            if (!(chatMessage instanceof SystemMessage)) {
                availableMessage.add(chatMessage);
            }
        }
        String json = messagesToJson(availableMessage);
        map.put((String) memoryId, json);
        db.commit();
    }

    @Override
    public void deleteMessages(Object memoryId) {
        map.remove((String) memoryId);
        db.commit();
    }

    public static MapDBChatMemoryStore getSingleton() {
        if (null == singleton) {
            synchronized (MapDBChatMemoryStore.class) {
                if (null == singleton) {
                    singleton = new MapDBChatMemoryStore();
                }
            }
        }
        return singleton;
    }
}
