package com.example.user.service;

public interface EmailService {
    void sendRegisterVerificationCode(String email, String code);

    void sendMail(String email, String subject, String content);
}
