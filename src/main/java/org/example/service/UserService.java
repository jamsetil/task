package org.example.service;

public interface UserService {
    void authenticate(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);
}
