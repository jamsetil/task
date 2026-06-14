package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.AuthValidator;
import org.example.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String authenticate(String username, String password) {
        authValidator.requireAuthentication(username, password);
        return jwtUtil.generateToken(username);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authValidator.requireCurrentUser(username);
        authValidator.requireAuthentication(username, oldPassword);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void logout(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwtUtil.invalidateToken(authorizationHeader.substring(7));
        }
    }
}
