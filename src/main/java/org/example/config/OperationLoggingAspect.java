package org.example.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
public class OperationLoggingAspect {

    @Around("execution(* org.example.service.impl..*(..))")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String transactionId = MDC.get(TransactionLoggingInterceptor.TRANSACTION_ID);
        String operation = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        org.slf4j.LoggerFactory.getLogger(joinPoint.getTarget().getClass()).info(
                "[transactionId={}] Operation start {} request={}",
                transactionId, operation, formatArgs(args));

        try {
            Object result = joinPoint.proceed();
            org.slf4j.LoggerFactory.getLogger(joinPoint.getTarget().getClass()).info(
                    "[transactionId={}] Operation success {} response={}",
                    transactionId, operation, summarizeResult(result));
            return result;
        } catch (Throwable ex) {
            org.slf4j.LoggerFactory.getLogger(joinPoint.getTarget().getClass()).warn(
                    "[transactionId={}] Operation failed {} error={}",
                    transactionId, operation, ex.getMessage());
            throw ex;
        }
    }

    static String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(OperationLoggingAspect::sanitizeArg)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    static String sanitizeArg(Object arg) {
        if (arg == null) {
            return "null";
        }
        if (arg instanceof String value) {
            if (value.regionMatches(true, 0, "Bearer ", 0, 7)) {
                return "Bearer ***";
            }
            String lower = value.toLowerCase(Locale.ROOT);
            if (lower.contains("password") || lower.contains("token")) {
                return "***";
            }
            return value;
        }
        return arg.toString();
    }

    static String summarizeResult(Object result) {
        if (result == null) {
            return "null";
        }
        if (result instanceof Collection<?> collection) {
            return "collection(size=" + collection.size() + ")";
        }
        if (result instanceof Optional<?> optional) {
            return optional.map(OperationLoggingAspect::summarizeResult).orElse("empty");
        }
        String className = result.getClass().getSimpleName();
        if (className.contains("$$") || isEntityLike(result)) {
            return className;
        }
        return result.toString();
    }

    private static boolean isEntityLike(Object result) {
        return result.getClass().isAnnotationPresent(jakarta.persistence.Entity.class);
    }
}
