package org.example.service;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;

import java.util.List;

public interface TrainingService {
    Training createTraining(TrainingRequestDTO request, String password);

    List<Training> getAllTrainingsByTraineeUsername(String traineeUsername, String password,
                                                      TrainingCriteria criteria);

    List<Training> getAllTrainingsByTrainerUsername(String trainerUsername, String password,
                                                    TrainingCriteria criteria);
}
