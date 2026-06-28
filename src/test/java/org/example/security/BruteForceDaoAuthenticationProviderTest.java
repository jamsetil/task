package org.example.security;

import org.example.service.impl.LoginAttemptServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BruteForceDaoAuthenticationProviderTest {

    @Mock
    private LoginAttemptServiceImpl loginAttemptServiceImpl;

    @Test
    void authenticate_lockedAccount_throwsLockedException() {
        when(loginAttemptServiceImpl.isBlocked("john")).thenReturn(true);
        var provider = createProvider("encoded-password");

        assertThrows(LockedException.class,
                () -> provider.authenticate(new UsernamePasswordAuthenticationToken("john", "pwd")));
    }

    @Test
    void authenticate_validCredentials_succeeds() {
        when(loginAttemptServiceImpl.isBlocked("john")).thenReturn(false);
        var provider = createProvider(new BCryptPasswordEncoder().encode("pwd"));

        var authentication = provider.authenticate(
                new UsernamePasswordAuthenticationToken("john", "pwd"));

        assertEquals("john", authentication.getName());
    }

    @Test
    void authenticate_invalidCredentials_incrementsFailedAttempts() {
        when(loginAttemptServiceImpl.isBlocked("john")).thenReturn(false);
        var provider = createProvider(new BCryptPasswordEncoder().encode("pwd"));

        assertThrows(BadCredentialsException.class,
                () -> provider.authenticate(new UsernamePasswordAuthenticationToken("john", "wrong")));
    }

    private BruteForceDaoAuthenticationProvider createProvider(String encodedPassword) {
        UserDetailsService userDetailsService = username -> User.builder()
                .username(username)
                .password(encodedPassword)
                .authorities(List.of())
                .build();
        return new BruteForceDaoAuthenticationProvider(
                userDetailsService,
                new BCryptPasswordEncoder(),
                loginAttemptServiceImpl);
    }
}
