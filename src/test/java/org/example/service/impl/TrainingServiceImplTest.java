package org.example.service.impl;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainingRequestDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.validation.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    private static final String TRAINEE_USERNAME = "faiq.azizzade";
    private static final String TRAINER_USERNAME = "ilyas.azizzade";

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_persistsLinkedEntities() {
        var request = trainingRequest();

        var specialization = TrainingType.builder()
                .trainingTypeId(1L)
                .trainingTypeName("Body Building")
                .build();
        var trainee = activeTrainee("Faiq", "Azizzade");
        var trainer = activeTrainer("Ilyas", "Azizzade", specialization);
        var saved = Training.builder().trainingId("id-1").trainingName("Cardio").build();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(any(Training.class))).thenReturn(saved);

        var captor = ArgumentCaptor.forClass(Training.class);
        var result = trainingService.createTraining(request);

        assertEquals("id-1", result.getTrainingId());
        verify(trainingRepository).save(captor.capture());
        assertEquals(specialization, captor.getValue().getTrainingType());
        verify(requestValidator).validate(request);
    }

    @Test
    void createTraining_missingTrainee_throws() {
        var request = trainingRequest();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void createTraining_missingTrainer_throws() {
        var request = trainingRequest();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(activeTrainee("Faiq", "Azizzade")));
        when(trainerRepository.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void createTraining_inactiveTrainee_throws() {
        var request = trainingRequest();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(
                Trainee.builder()
                        .user(User.builder().firstName("Faiq").lastName("Azizzade").isActive(false).build())
                        .build()));
        when(trainerRepository.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(
                activeTrainer("Ilyas", "Azizzade", null)));

        assertThrows(IllegalStateException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void createTraining_inactiveTrainer_throws() {
        var request = trainingRequest();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(activeTrainee("Faiq", "Azizzade")));
        when(trainerRepository.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(
                Trainer.builder()
                        .user(User.builder().firstName("Ilyas").lastName("Azizzade").isActive(false).build())
                        .build()));

        assertThrows(IllegalStateException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void createTraining_missingSpecialization_throws() {
        var request = trainingRequest();

        when(traineeRepository.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(activeTrainee("Faiq", "Azizzade")));
        when(trainerRepository.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(
                activeTrainer("Ilyas", "Azizzade", null)));

        assertThrows(IllegalStateException.class, () -> trainingService.createTraining(request));
    }

    @Test
    void getAllTrainingsByTraineeUsername_delegatesToRepository() {
        var criteria = TrainingCriteria.builder().trainingType("Cardio").build();
        when(trainingRepository.findByTraineeUsername(TRAINEE_USERNAME, criteria)).thenReturn(List.of());

        assertTrue(trainingService.getAllTrainingsByTraineeUsername(TRAINEE_USERNAME, criteria).isEmpty());
        verify(requestValidator).validate(criteria);
    }

    @Test
    void getAllTrainingsByTrainerUsername_delegatesToRepository() {
        when(trainingRepository.findByTrainerUsername(TRAINER_USERNAME, null)).thenReturn(List.of());

        assertTrue(trainingService.getAllTrainingsByTrainerUsername(TRAINER_USERNAME, null).isEmpty());
        verify(requestValidator, never()).validate(any());
    }

    @Test
    void getAllTrainingsByTrainerUsername_withCriteria_validatesAndDelegates() {
        var criteria = TrainingCriteria.builder().traineeName("Faiq Azizzade").build();
        when(trainingRepository.findByTrainerUsername(TRAINER_USERNAME, criteria)).thenReturn(List.of());

        assertTrue(trainingService.getAllTrainingsByTrainerUsername(TRAINER_USERNAME, criteria).isEmpty());
        verify(requestValidator).validate(criteria);
    }

    private static TrainingRequestDTO trainingRequest() {
        return TrainingRequestDTO.builder()
                .traineeUsername(TRAINEE_USERNAME)
                .trainerUsername(TRAINER_USERNAME)
                .trainingName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();
    }

    private static Trainee activeTrainee(String firstName, String lastName) {
        return Trainee.builder()
                .user(User.builder()
                        .userName(TRAINEE_USERNAME)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build())
                .build();
    }

    private static Trainer activeTrainer(String firstName, String lastName, TrainingType specialization) {
        return Trainer.builder()
                .user(User.builder()
                        .userName(TRAINER_USERNAME)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build())
                .specialization(specialization)
                .build();
    }
}
