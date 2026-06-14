package org.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TransactionLoggingInterceptor implements HandlerInterceptor {

    public static final String TRANSACTION_ID = "transactionId";
    private static final Set<String> SENSITIVE_QUERY_PARAMS = Set.of(
            "password", "oldpassword", "newpassword", "pwd", "token");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String transactionId = UUID.randomUUID().toString();
        MDC.put(TRANSACTION_ID, transactionId);
        request.setAttribute(TRANSACTION_ID, transactionId);
        log.info("[transactionId={}] REST request {} {} query={}",
                transactionId, request.getMethod(), request.getRequestURI(),
                sanitizeQueryString(request.getQueryString()));
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

    static String sanitizeQueryString(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return queryString;
        }
        return Arrays.stream(queryString.split("&"))
                .map(TransactionLoggingInterceptor::sanitizeQueryParam)
                .collect(Collectors.joining("&"));
    }

    private static String sanitizeQueryParam(String param) {
        int separatorIndex = param.indexOf('=');
        if (separatorIndex <= 0) {
            return param;
        }
        String key = param.substring(0, separatorIndex).toLowerCase(Locale.ROOT);
        if (SENSITIVE_QUERY_PARAMS.contains(key)) {
            return param.substring(0, separatorIndex) + "=***";
        }
        return param;
    }
}
