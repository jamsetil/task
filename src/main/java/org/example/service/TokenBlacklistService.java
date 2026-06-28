package org.example.service;

public interface TokenBlacklistService {
    void blacklist(String token, long expiresAt);

    boolean isBlacklisted(String token);
}
