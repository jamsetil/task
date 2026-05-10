package org.example.service;

import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;

public interface TrainingService {
    Training createTraining(TrainingRequestDTO trainer);
    Training getTraining(String trainingId);
}
