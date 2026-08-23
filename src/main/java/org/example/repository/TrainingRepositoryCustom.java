package org.example.repository;

import org.example.dto.TrainingCriteria;
import org.example.model.Training;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Profile("!docker")

public interface TrainingRepositoryCustom {

    List<Training> findByTraineeUsername(String traineeUsername, TrainingCriteria criteria);

    List<Training> findByTrainerUsername(String trainerUsername, TrainingCriteria criteria);
}
