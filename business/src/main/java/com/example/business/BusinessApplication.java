package com.example.business;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan("com.example")
@MapperScan("com.example.business.mapper")
@EnableFeignClients("com.example.business.feign")
@EnableCaching
public class BusinessApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessApplication.class, args);
        initFlowRules();
    }

    private static void initFlowRules(){
        List<FlowRule> rules = new ArrayList<>();

        FlowRule rule = new FlowRule();
        rule.setResource("confirm");
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        rule.setCount(1);

        rules.add(rule);
        FlowRuleManager.loadRules(rules);

        // 用 Jmeter 发送 10个/秒 购票请求
        // 1. 看结果树：不跨秒的情况下只有一个请求是成功的，其他都会报错。如果这10个请求跨秒了，可能看到成功的请求会大于1个
        // 2. 看控制台：能看到 FlowException 异常的日志输出
    }
}
