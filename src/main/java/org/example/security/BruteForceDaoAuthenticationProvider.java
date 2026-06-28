package org.example.security;

import org.example.service.impl.LoginAttemptServiceImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BruteForceDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private final LoginAttemptServiceImpl loginAttemptServiceImpl;

    public BruteForceDaoAuthenticationProvider(UserDetailsService userDetailsService,
                                               PasswordEncoder passwordEncoder,
                                               LoginAttemptServiceImpl loginAttemptServiceImpl) {
        this.loginAttemptServiceImpl = loginAttemptServiceImpl;
        setUserDetailsService(userDetailsService);
        setPasswordEncoder(passwordEncoder);
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        if (loginAttemptServiceImpl.isBlocked(username)) {
            throw new LockedException("Account is locked. Try again in 5 minutes.");
        }
        try {
            Authentication result = super.authenticate(authentication);
            loginAttemptServiceImpl.loginSucceeded(username);
            return result;
        } catch (LockedException ex) {
            throw ex;
        } catch (AuthenticationException ex) {
            loginAttemptServiceImpl.loginFailed(username);
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
