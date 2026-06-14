package org.example.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenBlacklistServiceTest {

    @Test
    void blacklist_marksTokenAsInvalid() {
        var service = new TokenBlacklistService();

        service.blacklist("token-1");

        assertTrue(service.isBlacklisted("token-1"));
        assertFalse(service.isBlacklisted("token-2"));
    }
}
