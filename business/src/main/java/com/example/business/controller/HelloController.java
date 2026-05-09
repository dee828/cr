package com.example.business.controller;

import com.example.business.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello(){
        return "hello business module";
    }

    @Autowired
    TrainService trainService;

    @GetMapping("hi")
    public String hi() {
        trainService.testCache();
        return "hi business module";
    }
}
