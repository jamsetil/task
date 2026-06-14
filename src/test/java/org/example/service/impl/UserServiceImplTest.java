package org.example.service.impl;

import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.util.AuthValidator;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void authenticate_returnsJwtToken() {
        when(jwtUtil.generateToken("john")).thenReturn("jwt-token");

        String token = userService.authenticate("john", "pwd");

        assertEquals("jwt-token", token);
        verify(authValidator).requireAuthentication("john", "pwd");
    }

    @Test
    void changePassword_validatesOldPasswordAndUpdates() {
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(
                User.builder().userName("john").password("encoded").build()));
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        userService.changePassword("john", "old", "new");

        verify(authValidator).requireCurrentUser("john");
        verify(authValidator).requireAuthentication("john", "old");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_userNotFound_throws() {
        when(userRepository.findByUserName("missing")).thenReturn(Optional.empty());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> userService.changePassword("missing", "old", "new"));
    }

    @Test
    void logout_invalidatesToken() {
        userService.logout("Bearer jwt-token");

        verify(jwtUtil).invalidateToken("jwt-token");
    }
}
