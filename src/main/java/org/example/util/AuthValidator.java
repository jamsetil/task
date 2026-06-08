package org.example.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.UserDAO;
import org.example.exception.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserDAO userDAO;

    public void requireAuthentication(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new AuthenticationException("Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("Password is required");
        }
        try {
            userDAO.authenticate(username, password);
        } catch (RuntimeException ex) {
            throw new AuthenticationException("Invalid username or password");
        }
        log.debug("Authenticated user username={}", username);
    }
}
