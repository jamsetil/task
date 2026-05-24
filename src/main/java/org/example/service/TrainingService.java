package org.example.service;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;

import java.util.List;

public interface TrainingService {
    Training createTraining(LoginRequestDTO auth, TrainingRequestDTO request);

    Training getTraining(LoginRequestDTO auth, String trainingId);

    List<Training> getAllTrainingsByTraineeUsername(
            LoginRequestDTO auth,
            String traineeUsername,
            TrainingCriteria criteria
    );

    List<Training> getAllTrainingsByTrainerUsername(
            LoginRequestDTO auth,
            String trainerUsername,
            TrainingCriteria criteria
    );
}
