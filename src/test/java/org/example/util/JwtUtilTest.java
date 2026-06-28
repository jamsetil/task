package org.example.util;

import org.example.service.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(
                "testSecretKeyForJwtGenerationThatShouldBeLongEnough123456",
                3600000,
                new TokenBlacklistServiceImpl());
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtUtil.generateToken("john");

        assertEquals("john", jwtUtil.extractUsername(token));
        assertTrue(jwtUtil.validateToken(token, "john"));
    }

    @Test
    void validateToken_rejectsBlacklistedToken() {
        String token = jwtUtil.generateToken("john");
        jwtUtil.invalidateToken(token);

        assertFalse(jwtUtil.validateToken(token, "john"));
    }

    @Test
    void validateToken_rejectsWrongUsername() {
        String token = jwtUtil.generateToken("john");

        assertFalse(jwtUtil.validateToken(token, "other"));
    }
}
