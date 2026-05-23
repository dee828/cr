package com.example.user.service.impl;

import com.example.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceOneImpl {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceOneImpl.class);

    public void loginNotify(User user) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("通知：{} 登录成功", user.getEmail());
    }
}
