package com.example.scheduler.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuartzJobDemo implements Job {
    private static final Logger log = LoggerFactory.getLogger(QuartzJobDemo.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("QuartzJobDemo.execute");
    }
}
