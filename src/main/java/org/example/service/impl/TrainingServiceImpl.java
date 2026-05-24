package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainingDAO;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainingRequestDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Training;
import org.example.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingDAO trainingDAO;

    @Override
    public Training createTraining(TrainingRequestDTO requestDTO) {

        log.info("Creating training record");

        Training training = Training.builder()
                .trainingDate(requestDTO.getTrainingDate())
                .trainingName(requestDTO.getTrainingName())
                .trainingDuration(requestDTO.getTrainingDuration())
                .build();

            trainingDAO.save(training);
//        log.info("Training created successfully with trainingId={}", training.getTrainingId());

        return null;
    }

    @Override
    public Training getTraining(String trainingId) {

        log.debug("Fetching training with trainingId={}", trainingId);

        return trainingDAO.find(trainingId)
                .orElseThrow(() -> {
                    log.error("Training not found with trainingId={}", trainingId);
                    return new ResourceNotFoundException("Training not found with id: " + trainingId);
                });
    }

    @Override
    public List<Training> getAllTrainingsByTraineeUsername(String traineeUsername,
                                                           TrainingCriteria criteria) {
        return trainingDAO.findTrainingsByTraineeUsername(traineeUsername, criteria);
    }
}