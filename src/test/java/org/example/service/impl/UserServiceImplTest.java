package org.example.service.impl;

import org.example.exception.AuthenticationException;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void authenticate_returnsJwtToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("john", null);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("john")).thenReturn("jwt-token");

        String token = userService.authenticate("john", "pwd");

        assertEquals("jwt-token", token);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticate_invalidCredentials_throws() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("bad"));

        assertThrows(AuthenticationException.class, () -> userService.authenticate("john", "bad"));
    }

    @Test
    void authenticate_lockedAccount_throws() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new LockedException("Account is locked. Try again in 5 minutes."));

        assertThrows(AuthenticationException.class, () -> userService.authenticate("john", "pwd"));
    }

    @Test
    void changePassword_validatesOldPasswordAndUpdates() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("john", null));
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(
                User.builder().userName("john").password("encoded").build()));
        when(passwordEncoder.encode("new")).thenReturn("encoded-new");

        userService.changePassword("john", "old", "new");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void changePassword_userNotFound_throws() {
        when(authenticationManager.authenticate(any()))
                .thenReturn(new UsernamePasswordAuthenticationToken("missing", null));
        when(userRepository.findByUserName("missing")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class,
                () -> userService.changePassword("missing", "old", "new"));
    }

    @Test
    void logout_invalidatesToken() {
        userService.logout("Bearer jwt-token");

        verify(jwtUtil).invalidateToken("jwt-token");
    }
}
