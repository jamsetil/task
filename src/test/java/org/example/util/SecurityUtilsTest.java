package org.example.util;

import org.example.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityUtilsTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsername_returnsAuthenticatedUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("john", null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))));

        assertEquals("john", SecurityUtils.getCurrentUsername());
    }

    @Test
    void getCurrentUsername_withoutAuthentication_throws() {
        assertThrows(UnauthorizedException.class, SecurityUtils::getCurrentUsername);
    }
}
