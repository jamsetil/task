package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {

    public static final String TRANSACTION_ID = "transactionId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);
        request.setAttribute(TRANSACTION_ID, transactionId);
        log.info("[transactionId={}] REST request {} {} query={}",
                transactionId, request.getMethod(), request.getRequestURI(), request.getQueryString());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        String transactionId = (String) request.getAttribute(TRANSACTION_ID);
        if (ex != null) {
            log.info("[transactionId={}] REST response {} {} status={} error={}",
                    transactionId, request.getMethod(), request.getRequestURI(), response.getStatus(), ex.getMessage());
        } else {
            log.info("[transactionId={}] REST response {} {} status={}",
                    transactionId, request.getMethod(), request.getRequestURI(), response.getStatus());
        }
        MDC.remove(TRANSACTION_ID);
    }
}
