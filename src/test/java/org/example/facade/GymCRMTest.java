package org.example.facade;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;
import org.example.model.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.data.MockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private TrainingRequestDTO trainingRequest;
    private Training training;

    private TrainerRequestDTO trainerRequest;
    private TrainerResponseDTO trainerResponse;

    private TraineeRequestDTO traineeRequest;
    private TraineeResponseDTO traineeResponse;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainingRequest = MockData.getTrainingRequestDTO();
        training = MockData.getTraining();

        trainerRequest = MockData.getTrainerRequestDTO();
        trainerResponse = new TrainerResponseDTO();

        traineeRequest = MockData.getTraineeRequestDTO();
        traineeResponse = new TraineeResponseDTO();

        trainee = MockData.getTrainee();
    }

    @Test
    void createTraining_shouldReturnTraining() {
        when(trainingService.createTraining(trainingRequest)).thenReturn(training);

        Training result = gymCRM.createTraining(trainingRequest);

        assertEquals(training, result);
        verify(trainingService).createTraining(trainingRequest);
    }

    @Test
    void getTraining_shouldCallService() {
        when(trainingService.getTraining("1")).thenReturn(training);

        Training result = gymCRM.getTraining("1");

        assertEquals(training, result);
        verify(trainingService).getTraining("1");
    }

    @Test
    void createTrainer_shouldReturnResponse() {
        when(trainerService.createTrainer(trainerRequest)).thenReturn(trainerResponse);

        TrainerResponseDTO result = gymCRM.createTrainer(trainerRequest);

        assertEquals(trainerResponse, result);
        verify(trainerService).createTrainer(trainerRequest);
    }

    @Test
    void updateTrainer_shouldCallService() {
        doNothing().when(trainerService).updateTrainer("ilyas", trainerRequest);

        gymCRM.updateTrainer("ilyas", trainerRequest);

        verify(trainerService).updateTrainer("ilyas", trainerRequest);
    }

    @Test
    void getTrainer_shouldReturnResponse() {
        when(trainerService.getTrainer("1")).thenReturn(trainerResponse);

        TrainerResponseDTO result = gymCRM.getTrainer("1");

        assertEquals(trainerResponse, result);
        verify(trainerService).getTrainer("1");
    }

    @Test
    void createTrainee_shouldReturnResponse() {
        when(traineeService.createTrainee(traineeRequest)).thenReturn(traineeResponse);

        TraineeResponseDTO result = gymCRM.createTrainee(traineeRequest);

        assertEquals(traineeResponse, result);
        verify(traineeService).createTrainee(traineeRequest);
    }

    @Test
    void updateTrainee_shouldCallService() {
        doNothing().when(traineeService).updateTrainee("ilyas", traineeRequest);

        gymCRM.updateTrainee("ilyas", traineeRequest);

        verify(traineeService).updateTrainee("ilyas", traineeRequest);
    }

    @Test
    void deleteTrainee_shouldCallService() {
        doNothing().when(traineeService).deleteTrainee("ilyas");

        gymCRM.deleteTrainee("ilyas");

        verify(traineeService).deleteTrainee("ilyas");
    }

    @Test
    void getTrainee_shouldReturnEntity() {
        when(traineeService.getTrainee("ilyas")).thenReturn(trainee);

        Trainee result = gymCRM.getTrainee("ilyas");

        assertEquals(trainee, result);
        verify(traineeService).getTrainee("ilyas");
    }
}