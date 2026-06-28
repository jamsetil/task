package org.example.service.impl;

import org.example.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final int maxAttempts;
    private final long lockoutDurationMs;
    private final ConcurrentHashMap<String, Integer> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lockoutUntil = new ConcurrentHashMap<>();

    public LoginAttemptServiceImpl(
            @Value("${security.login.max-attempts}") int maxAttempts,
            @Value("${security.login.lockout-duration-minutes}") int lockoutDurationMinutes) {
        this.maxAttempts = maxAttempts;
        this.lockoutDurationMs = lockoutDurationMinutes * 60L * 1000L;
    }

    public void loginSucceeded(String username) {
        attempts.remove(username);
        lockoutUntil.remove(username);
    }

    public void loginFailed(String username) {
        int count = attempts.merge(username, 1, Integer::sum);
        if (count >= maxAttempts) {
            lockoutUntil.put(username, System.currentTimeMillis() + lockoutDurationMs);
            attempts.remove(username);
        }
    }

    public boolean isBlocked(String username) {
        Long until = lockoutUntil.get(username);
        if (until == null) {
            return false;
        }
        if (System.currentTimeMillis() > until) {
            lockoutUntil.remove(username);
            return false;
        }
        return true;
    }
}
