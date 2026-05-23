package com.example.user.service.impl;

import com.example.user.entity.User;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(nameServer = "${rocketmq.name-server}", topic = "demo-topic", consumerGroup = "consumerGroup_2")
public class DemoServiceTowImpl implements RocketMQListener<User> {
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

    @Override
    public void onMessage(User user) {
        log.info("收到消息：{}", user);
        awardPoints(user);
    }
}
