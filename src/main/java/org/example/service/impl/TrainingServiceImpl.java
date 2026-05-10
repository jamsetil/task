package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDAO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;
import org.example.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingDAO trainingDAO;

    @Override
    public Training createTraining(TrainingRequestDTO requestDTO) {

        log.info("Creating training with name={}, traineeId={}, trainerId={}",
                requestDTO.getTrainingName(),
                requestDTO.getTraineeId(),
                requestDTO.getTrainerId());

        Training training = Training.builder()
                .trainingId(UUID.randomUUID().toString())
                .trainingDate(requestDTO.getTrainingDate())
                .trainingName(requestDTO.getTrainingName())
                .trainingDuration(requestDTO.getTrainingDuration())
                .traineeId(requestDTO.getTraineeId())
                .trainerId(requestDTO.getTrainerId())
                .build();

        Training saved = trainingDAO.save(training);

        log.info("Training created successfully with trainingId={}", training.getTrainingId());

        return saved;
    }

    @Override
    public Training getTraining(String trainingId) {

        log.debug("Fetching training with trainingId={}", trainingId);

        return trainingDAO.find(trainingId)
                .orElseThrow(() -> {
                    log.error("Training not found with trainingId={}", trainingId);
                    return new RuntimeException("Training not found with id: " + trainingId);
                });
    }
}