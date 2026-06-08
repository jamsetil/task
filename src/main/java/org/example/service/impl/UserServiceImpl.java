package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dao.UserDAO;
import org.example.service.UserService;
import org.example.util.AuthValidator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;
    private final AuthValidator authValidator;

    @Override
    public void authenticate(String username, String password) {
        authValidator.requireAuthentication(username, password);
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        authValidator.requireAuthentication(username, oldPassword);
        userDAO.changePassword(username, oldPassword, newPassword);
    }
}
