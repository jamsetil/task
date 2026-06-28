package org.example.service;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;

import java.util.List;

public interface TrainingService {
    Training createTraining(TrainingRequestDTO request, String authorizationHeader);

    List<Training> getAllTrainingsByTraineeUsername(String traineeUsername, TrainingCriteria criteria);

    List<Training> getAllTrainingsByTrainerUsername(String trainerUsername, TrainingCriteria criteria);
}
