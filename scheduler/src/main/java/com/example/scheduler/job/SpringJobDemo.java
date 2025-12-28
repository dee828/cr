package com.example.scheduler.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
//@EnableScheduling
public class SpringJobDemo {
    private static final Logger log = LoggerFactory.getLogger(SpringJobDemo.class);

//    @Scheduled(cron = "0/5 * * * * ?")
    public void job1(){
        log.info("SpringJobDemo.job1");
    }

    // Spring 自带的定时任务简单易用，但是它有一些缺点；适合单体小项目，快速实现定时任务
    // 这个项目是分布式应用，我们会使用 Quartz，适合分布式等更大型的项目
}
