package com.example.user.controller;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class RedisTestController {
    // 谁谁谁帮我做什么事情
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("redis/set/{key}/{value}")
    public String set(@PathVariable String key, @PathVariable String value) {
        stringRedisTemplate.opsForValue().set(key, value, 15, TimeUnit.SECONDS);
        return "ok! key=" + key + ", value=" + value;
    }

    @GetMapping("redis/get/{key}")
    public String get(@PathVariable String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
}
