package org.example.service.impl;

import org.example.dao.TrainerDAO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDAO trainerDAO;

    @Mock
    private CredentialGenerator generator;

    @Mock
    private UserProfileUpdater profileUpdater;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void shouldCreateTrainerSuccessfully() {

        // given
        TrainerRequestDTO request = MockData.getTrainerRequestDTO();

        when(generator.generateUsername("Ilyas", "Azizzade"))
                .thenReturn("Ilyas.Azizzade");

        when(generator.generatePassword())
                .thenReturn("password123");

        TrainerResponseDTO response =
                trainerService.createTrainer(request);


        assertNotNull(response);

        assertEquals("Ilyas", response.getFirstName());
        assertEquals("Azizzade", response.getLastName());
        assertEquals("Ilyas.Azizzade", response.getUserName());
        assertEquals("Fitness", response.getSpecialization());
        assertTrue(response.getIsActive());

        assertEquals(
                List.of(MockData.getTraining()),
                response.getTrainings()
        );

        verify(generator)
                .generateUsername("Ilyas", "Azizzade");

        verify(generator)
                .generatePassword();

        verify(trainerDAO, times(1))
                .save(any(Trainer.class));
    }

    @Test
    void shouldUpdateTrainerSuccessfully() {
        String userId = "1";

        Trainer trainer = MockData.getTrainer();

        TrainerRequestDTO request = MockData.getTrainerRequestDTO();

        when(trainerDAO.find(userId))
                .thenReturn(Optional.of(trainer));

        trainerService.updateTrainer(userId, request);

        verify(trainerDAO).find(userId);

        verify(profileUpdater)
                .updateUserProfile(request, trainer);

        assertTrue(trainer.getIsActive());

        assertEquals(
                "Fitness",
                trainer.getSpecialization()
        );

        assertEquals(
                List.of(MockData.getTraining()),
                trainer.getTrainings()
        );

        verify(trainerDAO)
                .update(userId, trainer);
    }

    @Test
    void shouldThrowExceptionWhenTrainerNotFound() {
        String userId = "1";

        TrainerRequestDTO request = MockData.getTrainerRequestDTO();

        when(trainerDAO.find(userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainerService.updateTrainer(userId, request)
        );

        assertEquals(
                "Trainer not found with id: 1",
                exception.getMessage()
        );

        verify(profileUpdater, never())
                .updateUserProfile(any(), any());

        verify(trainerDAO, never())
                .update(any(), any());
    }

    @Test
    void shouldReturnTrainerSuccessfully() {
        String userId = "1";

        Trainer trainer = Trainer.builder()
                .userId(userId)
                .firstName("John")
                .lastName("Smith")
                .userName("John.Smith")
                .isActive(true)
                .specialization("Fitness")
                .trainings(List.of(MockData.getTraining()))
                .build();

        when(trainerDAO.find(userId))
                .thenReturn(Optional.of(trainer));

        TrainerResponseDTO response =
                trainerService.getTrainer(userId);

        assertNotNull(response);

        assertEquals("1", response.getUserId());
        assertEquals("John", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("John.Smith", response.getUserName());
        assertEquals("Fitness", response.getSpecialization());

        assertTrue(response.getIsActive());

        assertEquals(
                List.of(MockData.getTraining()),
                trainer.getTrainings()
        );

        verify(trainerDAO).find(userId);
    }

    @Test
    void shouldThrowExceptionWhen_TrainerNotFound() {
        String userId = "1";

        when(trainerDAO.find(userId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainerService.getTrainer(userId)
        );

        assertEquals(
                "Trainer not found with username: 1",
                exception.getMessage()
        );

        verify(trainerDAO).find(userId);
    }


}