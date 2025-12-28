package com.example.common.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWT;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Lazy
public class LoginInterceptor implements HandlerInterceptor {
    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从用户的请求头中获取 token
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")){
            token = token.substring(7);
        }

        // 校验 token
        boolean b = validateToken(token);
        if(b){
            // 从token中解析出之前设置的邮箱等用户信息 [.setPayload("email", email)]
            Object idPayload = JWT.of(token).getPayload("id");
            Long id = Convert.toLong(idPayload);

            // 把当前用户信息设置到上下文（后续再做）
            UserContext.set(id);

            return true;
        }

        throw new CustomUnauthorizedException("认证不通过，用户校验失败");
    }

    private boolean validateToken(String token) {
        if(token == null || token.isBlank()){
            return false;
        }

        return JWT.of(token).setKey(secretKey.getBytes()).validate(0);
    }
}
