package org.example.security;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private GymUserDetailsService gymUserDetailsService;

    @Test
    void loadUserByUsername_traineeUser_hasTraineeRole() {
        when(userRepository.findByUserName("john")).thenReturn(Optional.of(
                User.builder().userName("john").password("encoded").isActive(true).build()));
        when(traineeRepository.findByUsername("john")).thenReturn(Optional.of(
                Trainee.builder().user(User.builder().userName("john").build()).build()));
        when(trainerRepository.findByUsername("john")).thenReturn(Optional.empty());

        var userDetails = gymUserDetailsService.loadUserByUsername("john");

        assertEquals("john", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TRAINEE")));
    }

    @Test
    void loadUserByUsername_unknownUser_throws() {
        when(userRepository.findByUserName("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername("missing"));
    }
}
