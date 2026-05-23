package com.example.user.service.impl;

import com.example.user.entity.User;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "demo-topic", consumerGroup = "consumerGroup_1")
public class DemoServiceOneImpl implements RocketMQListener<User> {
    private static final Logger log = LoggerFactory.getLogger(DemoServiceOneImpl.class);

    public void loginNotify(User user) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("通知：{} 登录成功", user.getEmail());
    }

    @Override
    public void onMessage(User user) {
        log.info("收到消息：{}", user);
        loginNotify(user);
    }
}
