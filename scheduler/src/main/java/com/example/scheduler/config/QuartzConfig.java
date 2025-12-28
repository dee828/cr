package com.example.scheduler.config;

import com.example.scheduler.job.QuartzJobDemo;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class QuartzConfig {
    /**
     * 任务
     * @return JobDetail
     */
    //@Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(QuartzJobDemo.class)
                .withIdentity("QuartzJobDemo", "g_job")
                .withDescription("QuartzJobDemo_Description")
                .storeDurably()
                .build();
    }

    /**
     * 触发器
     */
    //@Bean
    public Trigger trigger() {
        return TriggerBuilder.newTrigger().forJob(jobDetail())
                .withIdentity("QuartzJobDemo_Trigger", "g_trigger")
                .withDescription("QuartzJobDemo_Trigger_Description")
                .startNow()
                //.withSchedule(simpleSchedule().repeatForever().withIntervalInHours(1))
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                .build();
    }
}
