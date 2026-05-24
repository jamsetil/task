package org.example.service;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    Training createTraining(TrainingRequestDTO trainer);
    Training getTraining(String trainingId);

    List<Training> getAllTrainingsByTraineeUsername(String traineeUsername,
                                                    TrainingCriteria criteria);
}
