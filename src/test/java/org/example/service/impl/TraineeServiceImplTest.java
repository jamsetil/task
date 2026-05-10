package org.example.service.impl;

import org.example.dao.TraineeDAO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.model.Trainee;
import org.example.data.MockData;
import org.example.util.CredentialGenerator;
import org.example.util.UserProfileUpdater;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private UserProfileUpdater profileUpdater;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    void testCreateTrainee() {
        TraineeRequestDTO request = MockData.getTraineeRequestDTO();

        when(generator.generateUsername("Ilyas", "Azizzade"))
                .thenReturn("Ilyas.Azizzade");

        when(generator.generatePassword())
                .thenReturn("password123");

        TraineeResponseDTO response = traineeService.createTrainee(request);

        assertNotNull(response);

        assertEquals("Ilyas", response.getFirstName());
        assertEquals("Azizzade", response.getLastName());
        assertEquals("Ilyas.Azizzade", response.getUserName());
        assertEquals("Baku", response.getAddress());
        assertEquals(true, response.getIsActive());
        assertEquals(List.of(MockData.getTraining()), response.getTrainings());

        verify(generator).generateUsername("Ilyas", "Azizzade");
        verify(generator).generatePassword();

        verify(traineeDAO, times(1)).save(any(Trainee.class));

    }

    @Test
    void shouldUpdateTraineeSuccessfully() {
        String userId = "1";

        Trainee trainee = MockData.getTrainee();

        TraineeRequestDTO request = MockData.getTraineeRequestDTO();

        when(traineeDAO.find(userId))
                .thenReturn(Optional.of(trainee));


        traineeService.updateTrainee(userId, request);

        verify(traineeDAO).find(userId);

        verify(profileUpdater)
                .updateUserProfile(request, trainee);

        assertTrue(trainee.getIsActive());

        assertEquals("Baku", trainee.getAddress());

        assertEquals(
                List.of(MockData.getTraining()),
                trainee.getTrainings()
        );
    }

    @Test
    void shouldThrowExceptionWhenTraineeNotFound() {
        String userId = "1";

        TraineeRequestDTO request = MockData.getTraineeRequestDTO();

        when(traineeDAO.find(userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> traineeService.updateTrainee(userId, request)
        );

        assertEquals(
                "Trainee not found with id: 1",
                exception.getMessage()
        );

        verify(profileUpdater, never())
                .updateUserProfile(any(), any());
    }


    @Test
    void shouldDeleteTrainee() {
        String userId = "1";
        Trainee trainee = MockData.getTrainee();

        when(traineeDAO.find(userId))
                .thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee(userId);

        verify(traineeDAO, times(1))
                .find(userId);
        verify(generator, times(1))
                .removeUsername(trainee.getUserName());
        verify(traineeDAO, times(1))
                .delete(userId);
    }

    @Test
    void shouldReturnTrainee() {
        String userId = "1";

        Trainee trainee = MockData.getTrainee();

        when(traineeDAO.find(userId))
                .thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTrainee(userId);

        assertNotNull(result);

        assertEquals("Ilyas", result.getFirstName());
        assertEquals("Azizzade", result.getLastName());

        verify(traineeDAO).find(userId);
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingTrainee() {

        String userId = "1";

        when(traineeDAO.find(userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> traineeService.getTrainee(userId)
        );

        assertEquals(
                "Trainee not found with userId: 1",
                exception.getMessage()
        );

        verify(traineeDAO).find(userId);
    }

}