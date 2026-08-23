package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.AuthenticationException;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.JwtUtil;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Profile("!docker")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public String authenticate(String username, String password) {
        validateCredentialsPresent(username, password);
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            return jwtUtil.generateToken(authentication.getName());
        } catch (LockedException ex) {
            throw new AuthenticationException(ex.getMessage());
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new AuthenticationException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Changing password for user: {}", username);
        validateCredentialsPresent(username, oldPassword);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, oldPassword));
        } catch (LockedException ex) {
            throw new AuthenticationException(ex.getMessage());
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new AuthenticationException("Invalid username or password");
        }
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    private static void validateCredentialsPresent(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new AuthenticationException("Username is required");
        }
        if (password == null || password.isBlank()) {
            throw new AuthenticationException("Password is required");
        }
    }
}
