package org.example.service;

import org.example.service.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginAttemptServiceTest {

    private LoginAttemptServiceImpl loginAttemptServiceImpl;

    @BeforeEach
    void setUp() {
        loginAttemptServiceImpl = new LoginAttemptServiceImpl(3, 5);
    }

    @Test
    void loginFailed_blocksAfterMaxAttempts() {
        loginAttemptServiceImpl.loginFailed("john");
        loginAttemptServiceImpl.loginFailed("john");
        assertFalse(loginAttemptServiceImpl.isBlocked("john"));

        loginAttemptServiceImpl.loginFailed("john");
        assertTrue(loginAttemptServiceImpl.isBlocked("john"));
    }

    @Test
    void loginSucceeded_clearsAttempts() {
        loginAttemptServiceImpl.loginFailed("john");
        loginAttemptServiceImpl.loginFailed("john");
        loginAttemptServiceImpl.loginSucceeded("john");

        loginAttemptServiceImpl.loginFailed("john");
        loginAttemptServiceImpl.loginFailed("john");
        assertFalse(loginAttemptServiceImpl.isBlocked("john"));
    }
}
