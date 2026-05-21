package com.example.business.controller;

import cn.hutool.core.util.IdUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/get")
    public Map<String, Object> getCaptcha() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 生成验证码文本
            String text = defaultKaptcha.createText();
            String key = IdUtil.getSnowflakeNextIdStr();
            // 存入 Redis，5分钟有效
            redisTemplate.opsForValue().set("captcha:" + key, text, 5, TimeUnit.MINUTES);
            // 生成验证码图片
            BufferedImage image = defaultKaptcha.createImage(text);
            // 将图片转换为 base64
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", outputStream);
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            Map<String, Object> data = new HashMap<>();
            data.put("key", key);
            data.put("image", "data:image/jpeg;base64," + base64Image);
            result.put("code", 200);
            result.put("msg", "获取验证码成功");
            result.put("data", data);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "获取验证码失败");
            result.put("data", null);
        }
        return result;
    }

    @PostMapping("/check")
    public Map<String, Object> checkCaptcha(@RequestBody CaptchaRequest req) {
        Map<String, Object> result = new HashMap<>();
        String realCode = redisTemplate.opsForValue().get("captcha:" + req.getKey());
        if (realCode == null) {
            result.put("code", 500);
            result.put("msg", "验证码已过期");
            result.put("data", false);
            return result;
        }
        if (!realCode.equalsIgnoreCase(req.getImageCode())) {
            result.put("code", 500);
            result.put("msg", "验证码错误");
            result.put("data", false);
            return result;
        }
        // 校验通过后删除
        redisTemplate.delete("captcha:" + req.getKey());

        result.put("code", 200);
        result.put("msg", "验证码正确");
        result.put("data", true);

        return result;
    }

    @Setter
    @Getter
    public static class CaptchaRequest {
        private String key;
        private String imageCode;
    }
} 