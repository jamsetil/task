package org.example.util;

import org.example.exception.AuthenticationException;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthValidator authValidator;

    @Test
    void requireAuthentication_validCredentials_passes() {
        when(userRepository.findByUserNameAndPassword("john", "pwd"))
                .thenReturn(Optional.of(User.builder().userName("john").build()));
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
        when(userRepository.findByUserNameAndPassword("john", "bad")).thenReturn(Optional.empty());
        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication("john", "bad"));
    }
}
