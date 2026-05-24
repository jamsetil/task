package org.example.facade;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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

    private final LoginRequestDTO auth = LoginRequestDTO.builder().username("user").password("pwd").build();

    @Test
    void traineeMatcher_delegatesToService() {
        when(traineeService.matchTrainee("user", "pwd")).thenReturn(true);
        assertTrue(gymCRM.traineeMatcher(auth));
    }

    @Test
    void trainerMatcher_delegatesToService() {
        when(trainerService.matchCredentials("user", "pwd")).thenReturn(true);
        assertTrue(gymCRM.trainerMatcher(auth));
    }

    @Test
    void createTrainee_delegatesToService() {
        var request = TraineeCreateRequestDTO.builder().firstName("John").lastName("Smith").isActive(true).build();
        var response = TraineeResponseDTO.builder().userName("john.smith").build();
        when(traineeService.createTrainee(request)).thenReturn(response);
        assertEquals("john.smith", gymCRM.createTrainee(request).getUserName());
    }

    @Test
    void createTrainer_delegatesToService() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Jane").lastName("Doe").isActive(true).specializationName("Yoga").build();
        var response = TrainerResponseDTO.builder().userName("jane.doe").build();
        when(trainerService.createTrainer(request)).thenReturn(response);
        assertEquals("jane.doe", gymCRM.createTrainer(request).getUserName());
    }

    @Test
    void getTrainee_delegatesToService() {
        var trainee = Trainee.builder().build();
        when(traineeService.getTrainee(auth, "user")).thenReturn(trainee);
        assertSame(trainee, gymCRM.getTrainee(auth, "user"));
    }

    @Test
    void getTrainer_delegatesToService() {
        var trainer = Trainer.builder().build();
        when(trainerService.getTrainer(auth, "user")).thenReturn(trainer);
        assertSame(trainer, gymCRM.getTrainer(auth, "user"));
    }

    @Test
    void updateTrainee_delegatesToService() {
        var request = TraineeRequestDTO.builder().address("new").build();
        gymCRM.updateTrainee(auth, "user", request);
        verify(traineeService).updateTrainee(auth, "user", request);
    }

    @Test
    void updateTrainer_delegatesToService() {
        var request = TrainerRequestDTO.builder().firstName("New").build();
        gymCRM.updateTrainer(auth, "user", request);
        verify(trainerService).updateTrainer(auth, "user", request);
    }

    @Test
    void toggleTraineeStatus_delegatesToService() {
        var trainee = Trainee.builder().build();
        when(traineeService.changeStatus(auth, "user")).thenReturn(trainee);
        assertSame(trainee, gymCRM.toggleTraineeStatus(auth, "user"));
    }

    @Test
    void trainerToggleStatus_delegatesToService() {
        var trainer = Trainer.builder().build();
        when(trainerService.toggleTrainerStatus(auth, "user")).thenReturn(trainer);
        assertSame(trainer, gymCRM.trainerToggleStatus(auth, "user"));
    }

    @Test
    void deleteTrainee_delegatesToService() {
        gymCRM.deleteTrainee(auth, "user");
        verify(traineeService).deleteTrainee(auth, "user");
    }

    @Test
    void changeTraineePassword_delegatesToService() {
        when(traineeService.changePassword(auth, "old", "new")).thenReturn(true);
        assertTrue(gymCRM.changeTraineePassword(auth, "old", "new"));
    }

    @Test
    void changeTrainerPassword_delegatesToService() {
        when(trainerService.changePassword(auth, "old", "new")).thenReturn(true);
        assertTrue(gymCRM.changeTrainerPassword(auth, "old", "new"));
    }

    @Test
    void getTraineeTrainingsByCriteria_delegatesToService() {
        var criteria = TrainingCriteria.builder().trainingType("Cardio").build();
        when(trainingService.getAllTrainingsByTraineeUsername(auth, "user", criteria)).thenReturn(List.of());
        assertTrue(gymCRM.getTraineeTrainingsByCriteria(auth, "user", criteria).isEmpty());
    }

    @Test
    void getTrainerTrainingsByCriteria_delegatesToService() {
        var criteria = TrainingCriteria.builder().traineeName("John").build();
        when(trainingService.getAllTrainingsByTrainerUsername(auth, "user", criteria)).thenReturn(List.of());
        assertTrue(gymCRM.getTrainerTrainingsByCriteria(auth, "user", criteria).isEmpty());
    }

    @Test
    void createTraining_delegatesToService() {
        var request = TrainingRequestDTO.builder().trainingName("Cardio").build();
        var training = Training.builder().trainingId("id").build();
        when(trainingService.createTraining(auth, request)).thenReturn(training);
        assertEquals("id", gymCRM.createTraining(auth, request).getTrainingId());
    }

    @Test
    void getTraining_delegatesToService() {
        var training = Training.builder().trainingId("id").build();
        when(trainingService.getTraining(auth, "id")).thenReturn(training);
        assertEquals("id", gymCRM.getTraining(auth, "id").getTrainingId());
    }

    @Test
    void getUnassignedTrainers_delegatesToService() {
        when(traineeService.getUnassignedTrainers(auth, "user")).thenReturn(List.of());
        assertTrue(gymCRM.getUnassignedTrainers(auth, "user").isEmpty());
    }

    @Test
    void updateTraineeTrainers_delegatesToService() {
        gymCRM.updateTraineeTrainers(auth, "user", List.of("trainer1"));
        verify(traineeService).updateTraineeTrainers(auth, "user", List.of("trainer1"));
    }
}
