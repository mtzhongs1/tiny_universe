package com.ailu.server.socket;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/3 下午4:49
 */

public interface SocketObservable {

    void notifyObserverAddUser(Long userId);

    void notifyObserverDeleteUser(Long userId);
}
