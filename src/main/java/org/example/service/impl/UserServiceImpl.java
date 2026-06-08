package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.AuthValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    @Override
    public void authenticate(String username, String password) {
        authValidator.requireAuthentication(username, password);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        authValidator.requireAuthentication(username, oldPassword);
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));
        user.setPassword(newPassword);
        userRepository.save(user);
    }
}
