package com.example.user.controller;

import com.example.common.exception.CustomValidationException;
import com.example.common.response.R;
import com.example.user.entity.User;
import com.example.user.request.EmailVerificationCodeRequest;
import com.example.user.request.LoginRequest;
import com.example.user.request.RegisterRequest;
import com.example.user.response.LoginResponse;
import com.example.user.service.AuthenticationService;
import com.example.user.service.EmailService;
import com.example.user.service.EmailVerificationService;
import com.example.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "认证接口", description = "AuthController")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    EmailService emailService;

    @Autowired
    EmailVerificationService emailVerificationService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("register")
    @Operation(summary = "用户注册", description = "注册时需要通过邮箱接收验证码")
    public R<Object> register(@RequestBody @Validated RegisterRequest request){
        // 校验用户提交的数据

        // 确认密码要跟密码一致
        if(!request.getPassword().equals(request.getConfirmPassword())){
            throw new CustomValidationException("两次密码输入不一致", 400);
//            return new R<>(400, "两次密码输入不一致", null);
        }

        emailVerificationService.validateAndConsumeRegisterCode(request.getEmail(), request.getVerificationCode());

        // 入库
        // 谁谁谁.帮我操作
        Boolean result = userService.register(request);

        // 响应给用户
        return new R<>(200, "注册成功", result);
    }

    @PostMapping("register/email/code")
    public R<Object> sendRegisterEmailCode(@RequestBody @Valid EmailVerificationCodeRequest request) throws Exception {
        // 验证邮箱是否已被注册
        userService.ensureEmailNotRegistered(request.getEmail());

        String code = emailVerificationService.generateAndSaveRegisterCode(request.getEmail());

        // 谁谁谁帮我发送邮件
        emailService.sendRegisterVerificationCode(request.getEmail(), code);

        return R.ok();
    }

    @PostMapping("login")
    public R<LoginResponse> login(@RequestBody @Valid LoginRequest request){

        User user = authenticationService.authenticate(request.getEmail(), request.getPassword());

        String token = authenticationService.generateToken(user);

        // 返回特定格式的内容给前端
        LoginResponse response = LoginResponse.builder()
                .email(request.getEmail())
                .token(token)
                .build();

        return R.ok(response);
    }
}
