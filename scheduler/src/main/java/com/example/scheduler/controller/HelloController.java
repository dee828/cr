package com.example.scheduler.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello(){
        return "hello scheduler module";
    }

    @GetMapping("hi")
    public String hi(){
        return "hi scheduler module";
    }
}
