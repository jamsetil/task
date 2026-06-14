package org.example.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.AuthenticationException;
import org.example.exception.UnauthorizedException;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.service.LoginAttemptService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public void requireAuthentication(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new AuthenticationException("Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("Password is required");
        }
        if (loginAttemptService.isBlocked(username)) {
            throw new AuthenticationException("Account is locked. Try again in 5 minutes.");
        }

        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(username);
                    return new AuthenticationException("Invalid username or password");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            loginAttemptService.loginFailed(username);
            throw new AuthenticationException("Invalid username or password");
        }

        loginAttemptService.loginSucceeded(username);
        log.debug("Authenticated user username={}", username);
    }

    public void requireCurrentUser(String username) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!currentUsername.equals(username)) {
            throw new UnauthorizedException("Access denied");
        }
    }
}
