package com.example.user.controller;

import com.example.user.service.EmailService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailTestController {
    @Autowired
    EmailService emailService;

    @GetMapping("send-mail")
    public String send(@Parameter(description = "收件人地址") String to, String subject, String content){
        // 谁谁谁帮我发送邮件（通用的邮件）
        emailService.sendMail(to, subject, content);

        return "ok";
    }
}
