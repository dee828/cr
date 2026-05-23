package com.example.user.service.impl;

import com.example.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceTowImpl {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceTowImpl.class);

    public void awardPoints(User user) {
        awardPoints(user, 100);
    }

    public void awardPoints(User user, Integer points) {
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("积分发放：{} 增加 {} 分", user.getEmail(), points);
    }
}
