package org.example.service.impl;

import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.util.AuthValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
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
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(
                User.builder().userName("john").password("old").build()));

        userService.changePassword("john", "old", "new");

        verify(authValidator).requireAuthentication("john", "old");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_userNotFound_throws() {
        when(userRepository.findByUserName("missing")).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> userService.changePassword("missing", "old", "new"));
    }
}
