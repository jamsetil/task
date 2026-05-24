package org.example.facade;

import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymCRMTest {

    @Mock
    private TrainingService trainingService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private GymCRM gymCRM;

    @Test
    void traineeMatcher_delegatesToService() {
        var login = LoginRequestDTO.builder().username("john").password("pwd").build();
        when(traineeService.matchTrainee("john", "pwd")).thenReturn(true);

        assertTrue(gymCRM.traineeMatcher(login));
    }

    @Test
    void createTrainee_delegatesToService() {
        var request = TraineeCreateRequestDTO.builder().firstName("John").lastName("Smith").isActive(true).build();
        var response = TraineeResponseDTO.builder().userName("john.smith").password("pwd").build();
        when(traineeService.createTrainee(request)).thenReturn(response);

        assertEquals("john.smith", gymCRM.createTrainee(request).getUserName());
    }

    @Test
    void trainerMatcher_delegatesToService() {
        var login = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainerService.matchCredentials("trainer", "pwd")).thenReturn(true);

        assertTrue(gymCRM.trainerMatcher(login));
    }

    @Test
    void updateTraineeTrainers_delegatesToService() {
        var auth = LoginRequestDTO.builder().username("john").password("pwd").build();
        gymCRM.updateTraineeTrainers(auth, "john", java.util.List.of("trainer1"));

        verify(traineeService).updateTraineeTrainers(auth, "john", java.util.List.of("trainer1"));
    }
}
