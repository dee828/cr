package com.example.business.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;

@Component
public class CustomBlockExceptionHandler implements BlockExceptionHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        // Return 429 (Too Many Requests) by default.
        response.setStatus(429);
        response.setContentType("application/json;charset=utf-8");

        PrintWriter out = response.getWriter();
        out.print("""
                {"code":429,"msg":"当前购票人数过多，请稍后再试 (flow limiting)","data":null}
                """);
        out.flush();
        out.close();
        // 后续可以根据不同的异常类型，提示不一样的文本内容
    }
}
