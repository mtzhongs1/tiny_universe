package com.ailu.service.impl.user;
import com.ailu.exception.BaseException;
import com.ailu.properties.EmailProperties;
import com.ailu.service.user.EmailService;
import com.ailu.util.CodeUtil;
import com.ailu.util.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: ailu
 * @Date: 2/6/2024 下午4:55
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSenderImpl mailSender;

    @Autowired
    private EmailProperties emailProperties;

    @Autowired
    private RedisCache redisCache;

    @Override
    public void sendEmailCode(String to) {
        if(StringUtils.isBlank(to)){
            throw new BaseException("邮箱不能为空");
        }
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setFrom(emailProperties.getFrom());
        smm.setTo(to);
        smm.setSubject("验证码");
        String code = CodeUtil.generateCode4();
        smm.setText(code);
        redisCache.setCacheObject("code：" + to,code,5, TimeUnit.MINUTES);
        mailSender.send(smm);
    }
}
