package org.example.service.impl;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dao.TrainingTypeDAO;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.security.AuthValidator;
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
class TrainingServiceImplTest {

    @Mock
    private TrainingDAO trainingDAO;
    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_persistsLinkedEntities() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        var trainee = Trainee.builder()
                .user(User.builder().userName("john.smith").isActive(true).build())
                .build();
        var trainer = Trainer.builder()
                .user(User.builder().userName("ilyas.azizzade").isActive(true).build())
                .build();
        var type = TrainingType.builder().trainingTypeName("Cardio").build();
        var saved = Training.builder().trainingId("id-1").trainingName("Cardio").build();

        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDAO.find("ilyas.azizzade")).thenReturn(Optional.of(trainer));
        when(trainingTypeDAO.findByName("Cardio")).thenReturn(Optional.of(type));
        when(trainingDAO.save(any(Training.class))).thenReturn(saved);

        var result = trainingService.createTraining(auth, request);

        assertEquals("id-1", result.getTrainingId());
        verify(authValidator).requireTrainee(auth, "john.smith");
        verify(trainingDAO).save(any(Training.class));
    }

    @Test
    void createTraining_missingTrainee_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("missing")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        when(traineeDAO.find("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.createTraining(auth, request));
    }

    @Test
    void createTraining_missingTrainer_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("missing")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(
                Trainee.builder().user(User.builder().isActive(true).build()).build()));
        when(trainerDAO.find("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.createTraining(auth, request));
    }

    @Test
    void getAllTrainingsByTraineeUsername_delegatesToDao() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var criteria = TrainingCriteria.builder().trainingType("Cardio").build();
        when(trainingDAO.findTrainingsByTraineeUsername("john.smith", criteria)).thenReturn(java.util.List.of());

        assertTrue(trainingService.getAllTrainingsByTraineeUsername(auth, "john.smith", criteria).isEmpty());
    }

    @Test
    void getAllTrainingsByTrainerUsername_delegatesToDao() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainingDAO.findTrainingsByTrainerUsername("trainer", null)).thenReturn(java.util.List.of());

        assertTrue(trainingService.getAllTrainingsByTrainerUsername(auth, "trainer", null).isEmpty());
    }

    @Test
    void createTraining_inactiveTrainee_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        var trainee = Trainee.builder()
                .user(User.builder().isActive(false).build())
                .build();
        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerDAO.find("ilyas.azizzade")).thenReturn(Optional.of(
                Trainer.builder().user(User.builder().isActive(true).build()).build()));
        when(trainingTypeDAO.findByName("Cardio")).thenReturn(Optional.of(
                TrainingType.builder().trainingTypeName("Cardio").build()));

        assertThrows(IllegalStateException.class, () -> trainingService.createTraining(auth, request));
    }

    @Test
    void createTraining_inactiveTrainer_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(
                Trainee.builder().user(User.builder().isActive(true).build()).build()));
        when(trainerDAO.find("ilyas.azizzade")).thenReturn(Optional.of(
                Trainer.builder().user(User.builder().isActive(false).build()).build()));
        when(trainingTypeDAO.findByName("Cardio")).thenReturn(Optional.of(
                TrainingType.builder().trainingTypeName("Cardio").build()));

        assertThrows(IllegalStateException.class, () -> trainingService.createTraining(auth, request));
    }

    @Test
    void createTraining_missingTrainingType_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingTypeName("Unknown")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        when(traineeDAO.find("john.smith")).thenReturn(Optional.of(
                Trainee.builder().user(User.builder().isActive(true).build()).build()));
        when(trainerDAO.find("ilyas.azizzade")).thenReturn(Optional.of(
                Trainer.builder().user(User.builder().isActive(true).build()).build()));
        when(trainingTypeDAO.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.createTraining(auth, request));
    }

    @Test
    void getTraining_returnsEntity() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        var training = Training.builder().trainingId("id-1").build();
        when(trainingDAO.find("id-1")).thenReturn(Optional.of(training));

        assertEquals("id-1", trainingService.getTraining(auth, "id-1").getTrainingId());
        verify(authValidator).requireTrainee(auth, "john.smith");
    }

    @Test
    void getTraining_notFound_throws() {
        var auth = LoginRequestDTO.builder().username("john.smith").password("pwd").build();
        when(trainingDAO.find("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainingService.getTraining(auth, "missing"));
    }
}
