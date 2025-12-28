package com.example.user.service.impl;

import com.example.user.service.EmailService;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String from;

    @Override
    public void sendRegisterVerificationCode(String email, String code) {

        this.sendMail(email, "注册验证码", "本次操作的验证码是：" + code + "(5分钟之内有效)");

        System.out.println("验证码：" + code + ", 已经发送到邮箱：" + email);
    }

    @Override
    public void sendMail(String email, String subject, String content) {
        // 把验证码发送到对应的邮箱
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(new InternetAddress(from));
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(content, true);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

        sender.send(message);
    }
}
