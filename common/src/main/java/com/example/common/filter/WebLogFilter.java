package com.example.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * WebLogFilter 用于拦截和记录所有 HTTP 请求和响应的详细日志。
 *
 * <p>
 * 核心功能：
 * <ul>
 * <li>使用 TraceId 将请求和响应日志关联起来，便于链路追踪。</li>
 * <li>使用 ContentCachingWrapper 包装请求和响应，以允许重复读取 Body 内容。</li>
 * <li>通过检查 Content-Type 避免记录二进制内容（如图片、文件上传）。</li>
 * <li>对过长的日志内容进行截断，并移除换行符，优化日志的可读性和存储。</li>
 * </ul>
 * 
 * <p>
 * 注意：该过滤器在请求处理完成后才会记录日志，因此无法捕获过滤器链中断前发生的异常。
 */
//@Component
public class WebLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(WebLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 包装请求和响应，以便在请求处理完成后可以再次读取 body 内容
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();
        // 生成唯一的 TraceId 用于关联请求和响应
        String traceId = UUID.randomUUID().toString();

        try {
            // 继续执行过滤器链
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            // 无论请求是否成功，都在最后记录请求和响应日志
            logRequest(requestWrapper, traceId);
            logResponse(responseWrapper, traceId, startTime);

            // 重要：将缓存的响应内容复制回原始响应流，否则客户端将收到空响应
            responseWrapper.copyBodyToResponse();
        }
    }

    /**
     * 记录请求日志
     */
    private void logRequest(ContentCachingRequestWrapper request, String traceId) {
        String body = getPayload(request.getContentAsByteArray(), request.getContentType());

        logger.info("Request [TraceId: {}, URL: {}, Method: {}, IP: {}, Body: {}]",
                traceId,
                request.getRequestURL(),
                request.getMethod(),
                request.getRemoteAddr(),
                body);
    }

    /**
     * 记录响应日志
     */
    private void logResponse(ContentCachingResponseWrapper response, String traceId, long startTime) {
        String body = getPayload(response.getContentAsByteArray(), response.getContentType());

        logger.info("Response [TraceId: {}, Status: {}, Spend Time: {}ms, Body: {}]",
                traceId,
                response.getStatus(),
                (System.currentTimeMillis() - startTime),
                body);
    }

    private String getPayload(byte[] content, String contentType) {
        String body = "";
        if (content.length > 0) {
            if (isCompatibleContentType(contentType)) {
                body = new String(content, StandardCharsets.UTF_8);
                body = truncate(body);
            } else {
                body = "[Binary Content or Non-Text File]";
            }
        }
        return body;
    }

    /**
     * 检查 Content-Type 是否为兼容的文本类型。
     * 只记录 JSON, XML, 普通文本, 和 Form 表单数据。
     */
    private boolean isCompatibleContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.contains("application/json")
                || contentType.contains("application/xml")
                || contentType.contains("text/")
                || contentType.contains("application/x-www-form-urlencoded");
    }

    /**
     * 截断字符串并移除换行符，防止日志过长或格式混乱。
     */
    private String truncate(String str) {
        if (str == null) {
            return "";
        }
        // 移除换行符，保持日志单行格式
        str = str.replaceAll("[\r\n]", "");
        // 限制最大长度为 2000 字符
        if (str.length() > 2000) {
            return str.substring(0, 2000) + "...(truncated)";
        }
        return str;
    }
}
