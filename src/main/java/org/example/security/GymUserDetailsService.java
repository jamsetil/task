package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Profile("!docker")
@RequiredArgsConstructor
public class GymUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        traineeRepository.findByUsername(username)
                .ifPresent(ignored -> authorities.add(new SimpleGrantedAuthority("ROLE_TRAINEE")));
        trainerRepository.findByUsername(username)
                .ifPresent(ignored -> authorities.add(new SimpleGrantedAuthority("ROLE_TRAINER")));
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(user.getIsActive()))
                .build();
    }
}
