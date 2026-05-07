package com.example.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
public class HelloController {
    @Value("${custom.name}")
    String customName;

    @Autowired
    Environment environment;

    @GetMapping("/")
    public String hello(){
        return "hello";
    }

    @GetMapping("hi")
    public String hi(){
        return "hi " + customName + " port=" + environment.getProperty("server.port");
    }
}
