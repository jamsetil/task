package org.example.service;

public interface UserService {
    String authenticate(String username, String password);

    void changePassword(String oldPassword, String newPassword);

}
