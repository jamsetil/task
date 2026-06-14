package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService(3, 5);
    }

    @Test
    void loginFailed_blocksAfterMaxAttempts() {
        loginAttemptService.loginFailed("john");
        loginAttemptService.loginFailed("john");
        assertFalse(loginAttemptService.isBlocked("john"));

        loginAttemptService.loginFailed("john");
        assertTrue(loginAttemptService.isBlocked("john"));
    }

    @Test
    void loginSucceeded_clearsAttempts() {
        loginAttemptService.loginFailed("john");
        loginAttemptService.loginFailed("john");
        loginAttemptService.loginSucceeded("john");

        loginAttemptService.loginFailed("john");
        loginAttemptService.loginFailed("john");
        assertFalse(loginAttemptService.isBlocked("john"));
    }
}
