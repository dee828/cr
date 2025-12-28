package com.example.user.service.impl;

import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.common.exception.CustomUnauthorizedException;
import com.example.common.exception.CustomValidationException;
import com.example.user.entity.User;
import com.example.user.service.AuthenticationService;
import com.example.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    UserService userService;

    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Value("${auth.jwt.expires-in}")
    private Long expiresIn;

    @Override
    public User authenticate(String email, String password) {
        QueryWrapper<User> qw = new QueryWrapper<User>().eq("email", email);
        User user = userService.getOne(qw);

        if(user == null){
            throw new CustomUnauthorizedException("邮箱或密码错误");
        }

        if(!user.getPassword().equals(password)){
            throw new CustomUnauthorizedException("邮箱或密码错误");
        }

        return user;
    }

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiresIn);

        String token = JWT.create()
                .setPayload("id", user.getId())
                .setPayload("email", user.getEmail())
                .setIssuedAt(now)
                .setExpiresAt(exp)
                .setKey(secretKey.getBytes())
                .sign();

        return token;
    }

    @Override
    public boolean validateToken(String token) {
        if(token == null || token.isBlank()){
            return false;
        }

        return JWT.of(token).setKey(secretKey.getBytes()).validate(0);
    }
}
