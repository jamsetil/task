package org.example.service;

public interface UserService {
    String authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void logout(String authorizationHeader);
}
