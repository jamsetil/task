package org.example.service.impl;

import org.example.service.TokenBlacklistService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("!docker")

public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    public void blacklist(String token, long expiresAt) {
        blacklistedTokens.put(token,expiresAt);
    }

    public boolean isBlacklisted(String token) {
        Long expiresAt = blacklistedTokens.get(token);

        if (expiresAt == null) {
            return false;
        }

        if (expiresAt < System.currentTimeMillis()) {
            blacklistedTokens.remove(token);
            return false;
        }

        return true;
    }

}
