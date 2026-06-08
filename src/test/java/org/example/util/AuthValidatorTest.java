package org.example.util;

import org.example.dao.UserDAO;
import org.example.model.base.User;
import org.example.exception.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthValidatorTest {

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private AuthValidator authValidator;

    @Test
    void requireAuthentication_validCredentials_passes() {
        when(userDAO.authenticate("john", "pwd")).thenReturn(User.builder().userName("john").build());
        assertDoesNotThrow(() -> authValidator.requireAuthentication("john", "pwd"));
    }

    @Test
    void requireAuthentication_blankUsername_throws() {
        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication(" ", "pwd"));
    }

    @Test
    void requireAuthentication_blankPassword_throws() {
        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication("john", " "));
    }

    @Test
    void requireAuthentication_invalidCredentials_throws() {
        when(userDAO.authenticate("john", "bad"))
                .thenThrow(new RuntimeException("Invalid username or password"));
        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication("john", "bad"));
    }
}
