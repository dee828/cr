package com.example.scheduler.job;

import com.example.scheduler.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@DisallowConcurrentExecution
public class DailyTrainJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(DailyTrainJob.class);

    @Resource
    BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("生成每日车次数据 - 开始");
        businessFeign.genDaily(LocalDate.now().plusDays(1));
        log.info("生成每日车次数据 - 结束");
    }
}
