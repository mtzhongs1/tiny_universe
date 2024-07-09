package com.ailu.server.socket;

import com.ailu.server.config.spring.SpringBeanContext;
import com.ailu.server.service.user.SocketServcie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * WebSocket服务
 */
@Component
@Slf4j
@ServerEndpoint("/ws/{sid}")
public class EchoChannel implements com.ailu.socket.SocketObservable {

    //存放会话对象
    private static Map<String, Session> sessionMap = new HashMap();

    private SocketServcie socketServcie;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        System.out.println("客户端：" + sid + "建立连接");
        //TODO,将所有客户端放在sessionMap中，sessionMap的key为sid,在此案例中sid为用户的id或名字
        sessionMap.put(sid, session);
        //TODO:因为WebSocket是多例，而Spring的Bean对象是单例的，在非单例类中注入一个单例的Bean是冲突的。所以通过以下方式是解决，SpringBootContext是自定义的
        socketServcie = SpringBeanContext.getContext().getBean(SocketServcie.class);
        notifyObserverAddUser(Long.parseLong(sid));
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        System.out.println("收到来自客户端：" + sid + "的信息:" + message);
    }

    /**
     * 连接关闭调用的方法
     *
     * @param sid
     */
    @OnClose
    public void onClose(@PathParam("sid") String sid) {
        System.out.println("连接断开:" + sid);
        sessionMap.remove(sid);
        notifyObserverDeleteUser(Long.parseLong(sid));
    }

    /**
     * 群发
     *
     * @param message
     */
    public void sendToAllClient(String message) {
        Collection<Session> sessions = sessionMap.values();
        for (Session session : sessions) {
            try {
                //服务器向客户端发送消息
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //TODO：向指定用户发送消息，receiveId表示接收消息的一方
    public boolean sendToClient(String message, String receiveId) {
        if (sessionMap.containsKey(receiveId)) {
            Session session = sessionMap.get(receiveId);
            try {
                session.getBasicRemote().sendText(message);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<String> getSids(){
        return new ArrayList<>(sessionMap.keySet());
    }

    @Override
    public void notifyObserverAddUser(Long userId) {
        socketServcie.addUser(userId);
    }

    @Override
    public void notifyObserverDeleteUser(Long userId) {
        socketServcie.deleteUser(userId);
    }
}
