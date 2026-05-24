package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingDAO;
import org.example.dao.TrainingTypeDAO;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.security.AuthValidator;
import org.example.service.TrainingService;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingDAO trainingDAO;
    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private RequestValidator requestValidator;

    @Override
    @Transactional
    public Training createTraining(LoginRequestDTO auth, TrainingRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);
        authValidator.requireTrainee(auth, requestDTO.getTraineeUsername());

        Trainee trainee = traineeDAO.find(requestDTO.getTraineeUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found: " + requestDTO.getTraineeUsername()));

        Trainer trainer = trainerDAO.find(requestDTO.getTrainerUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer not found: " + requestDTO.getTrainerUsername()));

        var trainingType = trainingTypeDAO.findByName(requestDTO.getTrainingTypeName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training type not found: " + requestDTO.getTrainingTypeName()));

        if (!Boolean.TRUE.equals(trainee.getUser().getIsActive())) {
            throw new IllegalStateException("Trainee profile is not active");
        }
        if (!Boolean.TRUE.equals(trainer.getUser().getIsActive())) {
            throw new IllegalStateException("Trainer profile is not active");
        }

        log.info("Creating training '{}' for trainee={} with trainer={}",
                requestDTO.getTrainingName(),
                requestDTO.getTraineeUsername(),
                requestDTO.getTrainerUsername());

        Training training = Training.builder()
                .trainingDate(requestDTO.getTrainingDate())
                .trainingName(requestDTO.getTrainingName())
                .trainingDuration(requestDTO.getTrainingDuration())
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        training = trainingDAO.save(training);
        log.info("Training created successfully with trainingId={}", training.getTrainingId());
        return training;
    }

    @Override
    public Training getTraining(LoginRequestDTO auth, String trainingId) {
        authValidator.requireTrainee(auth, auth.getUsername());
        log.debug("Fetching training with trainingId={}", trainingId);
        return trainingDAO.find(trainingId)
                .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + trainingId));
    }

    @Override
    public List<Training> getAllTrainingsByTraineeUsername(
            LoginRequestDTO auth,
            String traineeUsername,
            TrainingCriteria criteria
    ) {
        authValidator.requireTrainee(auth, traineeUsername);
        if (criteria != null) {
            requestValidator.validate(criteria);
        }
        log.debug("Fetching trainings for trainee username={}", traineeUsername);
        return trainingDAO.findTrainingsByTraineeUsername(traineeUsername, criteria);
    }

    @Override
    public List<Training> getAllTrainingsByTrainerUsername(
            LoginRequestDTO auth,
            String trainerUsername,
            TrainingCriteria criteria
    ) {
        authValidator.requireTrainer(auth, trainerUsername);
        if (criteria != null) {
            requestValidator.validate(criteria);
        }
        log.debug("Fetching trainings for trainer username={}", trainerUsername);
        return trainingDAO.findTrainingsByTrainerUsername(trainerUsername, criteria);
    }
}
