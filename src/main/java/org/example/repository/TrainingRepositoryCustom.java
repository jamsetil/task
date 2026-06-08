package org.example.repository;

import org.example.dto.TrainingCriteria;
import org.example.model.Training;

import java.util.List;

public interface TrainingRepositoryCustom {

    List<Training> findByTraineeUsername(String traineeUsername, TrainingCriteria criteria);

    List<Training> findByTrainerUsername(String trainerUsername, TrainingCriteria criteria);
}
