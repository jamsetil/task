package org.example.util;

import org.example.exception.AuthenticationException;
import org.example.exception.UnauthorizedException;
import org.example.model.base.User;
import org.example.repository.UserRepository;
import org.example.service.LoginAttemptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthValidatorTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthValidator authValidator;

    @Test
    void requireAuthentication_validCredentials_passes() {
        when(loginAttemptService.isBlocked("john")).thenReturn(false);
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(
                User.builder().userName("john").password("encoded").build()));
        when(passwordEncoder.matches("pwd", "encoded")).thenReturn(true);

        assertDoesNotThrow(() -> authValidator.requireAuthentication("john", "pwd"));
        verify(loginAttemptService).loginSucceeded("john");
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
        when(loginAttemptService.isBlocked("john")).thenReturn(false);
        when(userRepository.findByUserName("john")).thenReturn(Optional.empty());

        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication("john", "bad"));
        verify(loginAttemptService).loginFailed("john");
    }

    @Test
    void requireAuthentication_lockedAccount_throws() {
        when(loginAttemptService.isBlocked("john")).thenReturn(true);

        assertThrows(AuthenticationException.class, () -> authValidator.requireAuthentication("john", "pwd"));
    }

    @Test
    void requireCurrentUser_matchingUsername_passes() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        assertDoesNotThrow(() -> authValidator.requireCurrentUser("john"));
        SecurityContextHolder.clearContext();
    }

    @Test
    void requireCurrentUser_mismatchedUsername_throws() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        assertThrows(UnauthorizedException.class, () -> authValidator.requireCurrentUser("other"));
        SecurityContextHolder.clearContext();
    }
}
