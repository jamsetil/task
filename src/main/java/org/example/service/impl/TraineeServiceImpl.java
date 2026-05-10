package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.service.TraineeService;
import org.example.util.CredentialGenerator;
import org.example.util.UserProfileUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeDAO traineeDao;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private UserProfileUpdater profileUpdater;

    @Override
    public TraineeResponseDTO createTrainee(TraineeRequestDTO requestDTO) {

        log.info("Creating trainee profile");

        var trainee = Trainee.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .isActive(requestDTO.getIsActive())
                .userId(UUID.randomUUID().toString())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .userName(generator.generateUsername(
                        requestDTO.getFirstName(),
                        requestDTO.getLastName()
                ))
                .address(requestDTO.getAddress())
                .password(generator.generatePassword())
                .trainings(requestDTO.getTrainings())
                .build();

        traineeDao.save(trainee);

        log.info("Trainee created successfully with userId={}", trainee.getUserId());

        return TraineeResponseDTO.builder()
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth().toString())
                .firstName(trainee.getFirstName())
                .isActive(trainee.getIsActive())
                .lastName(trainee.getLastName())
                .trainings(trainee.getTrainings())
                .userId(trainee.getUserId())
                .userName(trainee.getUserName())
                .build();
    }

    @Override
    public void updateTrainee(String userId, TraineeRequestDTO requestDTO) {

        log.info("Updating trainee with userId={}", userId);

        var entity = traineeDao.find(userId)
                .orElseThrow(() -> {
                    log.error("Trainee not found for update, userId={}", userId);
                    return new ResourceNotFoundException("Trainee not found with id: " + userId);
                });

        profileUpdater.updateUserProfile(requestDTO, entity);

        entity.setIsActive(requestDTO.getIsActive());
        entity.setAddress(requestDTO.getAddress());
        entity.setTrainings(requestDTO.getTrainings());

        log.info("Trainee updated successfully, userId={}", userId);
    }

    @Override
    public void deleteTrainee(String userId) {

        log.warn("Deleting trainee with userId={}", userId);

        var trainee = traineeDao.find(userId)
                .orElseThrow(() -> {
                    log.error("Trainee not found for delete, userId={}", userId);
                    return new ResourceNotFoundException("Trainee not found with id: " + userId);
                });

        generator.removeUsername(trainee.getUserName());
        traineeDao.delete(userId);

        log.info("Trainee deleted successfully, userId={}", userId);
    }

    @Override
    public Trainee getTrainee(String userId) {

        log.debug("Fetching trainee with userId={}", userId);

        return traineeDao.find(userId)
                .orElseThrow(() -> {
                    log.error("Trainee not found, userId={}", userId);
                    return new ResourceNotFoundException("Trainee not found with userId: " + userId);
                });
    }
}