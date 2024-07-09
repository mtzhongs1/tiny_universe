package com.ailu.server.service.user;

/**
 * @author: ailu
 * @description: TODO
 * @date: 2/6/2024 下午4:53
 */

public interface EmailService {
    void sendEmailCode(String to);

    String checkCode(String email, String code);
}
