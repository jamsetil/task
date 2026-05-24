package org.example.service.impl;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.model.Trainee;
import org.example.model.base.User;
import org.example.security.AuthValidator;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void createTrainee_generatesCredentials() {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .address("Street 1")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .build();

        when(generator.generateUsername("John", "Smith")).thenReturn("john.smith");
        when(generator.generatePassword()).thenReturn("pwd1234567");
        doAnswer(invocation -> null).when(traineeDAO).save(any(Trainee.class));

        var response = traineeService.createTrainee(request);

        assertEquals("john.smith", response.getUserName());
        assertEquals("pwd1234567", response.getPassword());
        verify(traineeDAO).save(any(Trainee.class));
    }

    @Test
    void changeStatus_returnsUpdatedTrainee() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var trainee = Trainee.builder()
                .user(User.builder().userName("john.smith").isActive(true).build())
                .build();

        when(traineeDAO.toggleStatus("john.smith")).thenReturn(trainee);

        assertSame(trainee, traineeService.changeStatus(auth, "john.smith"));
        verify(authValidator).requireTrainee(auth, "john.smith");
    }

    @Test
    void changePassword_invalidOldPassword_returnsFalse() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        when(traineeDAO.changePassword("john.smith", "old", "new")).thenReturn(null);

        assertFalse(traineeService.changePassword(auth, "old", "new"));
    }

    @Test
    void updateTrainee_updatesProfileFields() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var user = User.builder().userName("john.smith").firstName("John").lastName("Smith").build();
        var trainee = Trainee.builder().user(user).address("old").build();
        var request = TraineeRequestDTO.builder().address("new").build();

        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeDAO.update("john.smith", trainee)).thenReturn(trainee);

        traineeService.updateTrainee(auth, "john.smith", request);

        assertEquals("new", trainee.getAddress());
    }

    @Test
    void matchTrainee_delegatesToDao() {
        when(traineeDAO.matchTrainee("john", "pwd")).thenReturn(true);
        assertTrue(traineeService.matchTrainee("john", "pwd"));
    }

    @Test
    void deleteTrainee_authenticated_deletesRecord() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        traineeService.deleteTrainee(auth, "john.smith");
        verify(traineeDAO).delete("john.smith");
    }

    @Test
    void getUnassignedTrainers_returnsDaoResult() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        when(trainerDAO.findTrainersNotAssignedToTrainee("john.smith")).thenReturn(java.util.List.of());

        assertTrue(traineeService.getUnassignedTrainers(auth, "john.smith").isEmpty());
    }

    @Test
    void getTrainee_returnsEntity() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var trainee = Trainee.builder().user(User.builder().userName("john.smith").build()).build();
        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(trainee));

        assertSame(trainee, traineeService.getTrainee(auth, "john.smith"));
    }

    @Test
    void changePassword_success_returnsTrue() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var trainee = Trainee.builder().user(User.builder().userName("john.smith").build()).build();
        when(traineeDAO.changePassword("john.smith", "old", "new")).thenReturn(trainee);

        assertTrue(traineeService.changePassword(auth, "old", "new"));
    }

    @Test
    void updateTraineeTrainers_delegatesToDao() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        traineeService.updateTraineeTrainers(auth, "john.smith", java.util.List.of("trainer1"));
        verify(traineeDAO).updateTraineeTrainers("john.smith", java.util.List.of("trainer1"));
    }
}
