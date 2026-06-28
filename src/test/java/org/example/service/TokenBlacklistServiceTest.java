package org.example.service;

import org.example.service.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBlacklistServiceTest {

    @Test
    void blacklist_marksTokenAsInvalid() {
        var service = new TokenBlacklistServiceImpl();

        long expiresAt = System.currentTimeMillis() + 3_600_000;
        service.blacklist("token-1", expiresAt);

        assertTrue(service.isBlacklisted("token-1"));
        assertFalse(service.isBlacklisted("token-2"));
    }
}
