package com.example.common.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.jwt.JWT;
import com.example.common.core.UserContext;
import com.example.common.exception.CustomUnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Lazy
public class LoginInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

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

            UserContext.set(id);

            return true;
        }

        throw new CustomUnauthorizedException("认证不通过，用户校验失败");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求结束后必须清理 ThreadLocal，防止线程重用导致的数据污染
        UserContext.remove();
    }

    private boolean validateToken(String token) {
        if(token == null || token.isBlank()){
            return false;
        }

        try {
            return JWT.of(token).setKey(secretKey.getBytes()).validate(0);
        } catch (Exception e) {
            log.error("token 校验异常", e);
            return false;
        }
    }
}
