package com.example.scheduler.controller;

import com.example.common.response.R;
import com.example.scheduler.request.CronJobRequest;
import com.example.scheduler.response.CronJobResponse;
import jakarta.validation.Valid;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("admin/job")
public class JobController {
    private static final Logger log = LoggerFactory.getLogger(JobController.class);

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @GetMapping("/list")
    public R<List<CronJobResponse>> list() {
        log.info("查询所有定时任务");
        R<List<CronJobResponse>> response = R.ok();
        List<CronJobResponse> cronJobResponse = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobResponse cronJobResp = new CronJobResponse();
                    cronJobResp.setName(jobKey.getName());
                    cronJobResp.setGroup(jobKey.getGroup());

                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);

                    cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobResp.setCronExpression(cronTrigger.getCronExpression());
                    cronJobResp.setDescription(cronTrigger.getDescription());

                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());

                    cronJobResp.setState(triggerState.name());

                    cronJobResponse.add(cronJobResp);
                }
            }
        } catch (SchedulerException e) {
            log.info("查看定时任务失败：", e);
            response.setCode(500);
            response.setMsg("查看定时任务失败：调度异常");
        } catch (Exception e) {
            log.info("查看定时任务失败：", e);
            response.setCode(500);
            response.setMsg("查看定时任务失败");
        }

        response.setData(cronJobResponse);
        log.info("查看定时任务完成");
        return response;
    }

    @PostMapping("/save")
    public R<Object> save(@Valid @RequestBody CronJobRequest cronJobRequest) {
        String jobClassName = cronJobRequest.getName();
        String jobGroupName = cronJobRequest.getGroup();
        String cronExpression = cronJobRequest.getCronExpression();
        String description = cronJobRequest.getDescription();
        log.info("创建定时任务：{}, {}, {}, {}", jobClassName, jobGroupName, cronExpression, description);

        R<Object> response = R.ok();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.start();
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName))
                    .withIdentity(jobClassName, jobGroupName)
                    .build();
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobClassName, jobGroupName)
                    .withDescription(description)
                    .withSchedule(cronScheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (ObjectAlreadyExistsException e) {
            log.error("创建定时任务失败：", e);
            response.setCode(500);
            response.setMsg("创建定时任务失败：任务已存在");
        }  catch (SchedulerException e) {
            log.error("创建定时任务失败：", e);
            response.setCode(500);
            response.setMsg("创建定时任务失败：调度异常");
        } catch (ClassNotFoundException e) {
            log.error("创建定时任务失败：", e);
            response.setCode(500);
            response.setMsg("创建定时任务失败：任务类名不存在");
        } catch (Exception e) {
            log.error("创建定时任务失败：", e);
            response.setCode(500);
            response.setMsg("创建定时任务失败");
        }

        log.info("创建定时任务完成");
        return response;
    }

    @PostMapping("/pause")
    public R<Object> pause(@Valid @RequestBody CronJobRequest cronJobRequest) {
        String jobClassName = cronJobRequest.getName();
        String jobGroupName = cronJobRequest.getGroup();
        log.info("暂停定时任务：{}, {}", jobClassName, jobGroupName);

        R<Object> response = R.ok();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            log.error("暂停定时任务失败：", e);
            response.setCode(500);
            response.setMsg("暂停定时任务失败：调度异常");
        }

        log.info("暂停定时任务完成");
        return response;
    }

    @PostMapping("/resume")
    public R<Object> resume(@Valid @RequestBody CronJobRequest cronJobRequest) {
        String jobClassName = cronJobRequest.getName();
        String jobGroupName = cronJobRequest.getGroup();
        log.info("重启定时任务：{}, {}", jobClassName, jobGroupName);

        R<Object> response = R.ok();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            log.error("重启定时任务失败：", e);
            response.setCode(500);
            response.setMsg("重启定时任务失败：调度异常");
        }

        log.info("重启定时任务完成");
        return response;
    }

    @PostMapping("/reschedule")
    public R<Object> reSchedule(@Valid @RequestBody CronJobRequest cronJobRequest) {
        String jobClassName = cronJobRequest.getName();
        String jobGroupName = cronJobRequest.getGroup();
        String cronExpression = cronJobRequest.getCronExpression();
        String description = cronJobRequest.getDescription();
        log.info("更新定时任务：{}, {}, {}, {}", jobClassName, jobGroupName, cronExpression, description);

        R<Object> response = R.ok();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            // 重新设置开始时间
            trigger.setStartTime(new Date());

            CronTrigger triggerNew = trigger;
            // 按新的 cronExpression 表达式重新构建 Trigger
            triggerNew = triggerNew.getTriggerBuilder()
                    .withIdentity(triggerKey)
                    .withDescription(description)
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.rescheduleJob(triggerKey, triggerNew);
        } catch (Exception e) {
            log.info("更新定时任务失败：", e);
            response.setCode(500);
            response.setMsg("更新定时任务失败：调度异常");
        }

        log.info("更新定时任务完成");
        return response;
    }

    @PostMapping("/delete")
    public R<Object> delete(@Valid @RequestBody CronJobRequest cronJobRequest) {
        String jobClassName = cronJobRequest.getName();
        String jobGroupName = cronJobRequest.getGroup();
        log.info("删除定时任务：{}, {}", jobClassName, jobGroupName);

        R<Object> response = R.ok();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            log.error("删除定时任务失败：", e);
            response.setCode(500);
            response.setMsg("删除定时任务失败：调度异常");
        }

        log.info("删除定时任务完成");
        return response;
    }

    @PostMapping("/run")
    public R<Object> run(@Valid @RequestBody CronJobRequest cronJobReq) throws SchedulerException {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        log.info("单独执行指定任务（计划外、一次性、手工补偿）：{}, {}", jobClassName, jobGroupName);

        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        return R.ok();
    }
}
