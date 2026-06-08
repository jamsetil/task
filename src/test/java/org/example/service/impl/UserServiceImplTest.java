package org.example.service.impl;

import org.example.dao.UserDAO;
import org.example.util.AuthValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;
    @Mock
    private AuthValidator authValidator;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void authenticate_validatesCredentials() {
        userService.authenticate("john", "pwd");

        verify(authValidator).requireAuthentication("john", "pwd");
    }

    @Test
    void changePassword_validatesOldPasswordAndUpdates() {
        userService.changePassword("john", "old", "new");

        verify(authValidator).requireAuthentication("john", "old");
        verify(userDAO).changePassword("john", "old", "new");
    }
}
