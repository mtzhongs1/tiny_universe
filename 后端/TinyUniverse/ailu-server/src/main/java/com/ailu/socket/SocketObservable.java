package com.ailu.socket;

import com.ailu.dto.user.MessageDTO;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2024/7/3 下午4:49
 */

public interface SocketObservable {

    void notifyObserverAddUser(Long userId);

    void notifyObserverDeleteUser(Long userId);
}
