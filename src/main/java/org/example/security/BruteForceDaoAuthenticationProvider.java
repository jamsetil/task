package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.service.LoginAttemptService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BruteForceDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final LoginAttemptService loginAttemptService;

    public BruteForceDaoAuthenticationProvider(UserDetailsService userDetailsService,
                                               PasswordEncoder passwordEncoder,
                                               LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (loginAttemptService.isBlocked(username)) {
            throw new LockedException("Account is locked. Try again in 5 minutes.");
        }
        try {
            Authentication result = super.authenticate(authentication);
            loginAttemptService.loginSucceeded(username);
            return result;
        } catch (LockedException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            loginAttemptService.loginFailed(username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
