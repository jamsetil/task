package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.dto.request.TrainingRequestDTO;
import org.example.enums.ActionType;
import org.example.exception.ResourceNotFoundException;
import org.example.messaging.WorkloadMessagePublisher;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.example.service.TrainingService;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingRepository trainingRepository;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private RequestValidator requestValidator;
    @Autowired
    private WorkloadMessagePublisher workloadMessagePublisher;

    @Override
    @Transactional
    public Training createTraining(TrainingRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);

        Trainee trainee = traineeRepository.findByUsername(requestDTO.getTraineeUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found: " + requestDTO.getTraineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(requestDTO.getTrainerUsername())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainer not found: " + requestDTO.getTrainerUsername()));

        if (!Boolean.TRUE.equals(trainee.getUser().getIsActive())) {
            throw new IllegalStateException("Trainee profile is not active");
        }
        if (!Boolean.TRUE.equals(trainer.getUser().getIsActive())) {
            throw new IllegalStateException("Trainer profile is not active");
        }
        if (trainer.getSpecialization() == null) {
            throw new IllegalStateException("Trainer specialization is not set");
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
                .trainingType(trainer.getSpecialization())
                .build();

        training = trainingRepository.save(training);

        workloadMessagePublisher.publish(TrainerWorkloadRequest.builder()
                .actionType(ActionType.ADD)
                .isActive(trainer.getUser().getIsActive())
                .trainingDate(requestDTO.getTrainingDate())
                .trainingDuration(requestDTO.getTrainingDuration())
                .trainerLastName(trainer.getUser().getLastName())
                .trainerFirstName(trainer.getUser().getFirstName())
                .trainerUsername(trainer.getUser().getUserName())
                .build());

        log.info("Training created successfully with trainingId={}", training.getTrainingId());
        return training;
    }

    @Override
    public List<Training> getAllTrainingsByTraineeUsername(String traineeUsername, TrainingCriteria criteria) {
        if (criteria != null) {
            requestValidator.validate(criteria);
        }
        log.debug("Fetching trainings for trainee username={}", traineeUsername);
        return trainingRepository.findByTraineeUsername(traineeUsername, criteria);
    }

    @Override
    public List<Training> getAllTrainingsByTrainerUsername(String trainerUsername, TrainingCriteria criteria) {
        if (criteria != null) {
            requestValidator.validate(criteria);
        }
        log.debug("Fetching trainings for trainer username={}", trainerUsername);
        return trainingRepository.findByTrainerUsername(trainerUsername, criteria);
    }
}
