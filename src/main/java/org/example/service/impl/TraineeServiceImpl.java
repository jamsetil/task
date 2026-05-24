package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.model.base.User;
import org.example.service.TraineeService;
import org.example.util.CredentialGenerator;
import org.example.util.UserProfileUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public TraineeResponseDTO createTrainee(TraineeCreateRequestDTO requestDTO) {

        log.info("creaiting user profile for trainee");
        User user = User.builder()
                        .userName(requestDTO.getUserName())
                                .firstName(requestDTO.getFirstName())
                                        .lastName(requestDTO.getLastName())
                                                .isActive(requestDTO.getIsActive())
                                                        .password(generator.generatePassword())
                                                                .build();


        log.info("Creating trainee profile");
        Trainee trainee = Trainee.builder()
                .address(requestDTO.getAddress())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .user(user)
                .build();

        traineeDao.save(trainee);


        log.info("Trainee created successfully with traineeId={}", trainee.getTraineeId());

        return TraineeResponseDTO.builder()
                .build();
    }

    @Override
    public void updateTrainee(String username, TraineeRequestDTO requestDTO) {
        log.info("Updating trainee with username={}", username);

        Trainee trainee = traineeDao.find(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for update, username={}", username);
                    return new ResourceNotFoundException(
                            "Trainee not found with username: " + username
                    );
                });

        if (requestDTO.getFirstName() != null) {
            trainee.getUser().setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            trainee.getUser().setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getUsername() != null) {
            trainee.getUser().setUserName(requestDTO.getUsername());
        }


        if (requestDTO.getIsActive() != null) {
            trainee.getUser().setIsActive(requestDTO.getIsActive());
        }

        if (requestDTO.getDateOfBirth() != null) {
            trainee.setDateOfBirth(requestDTO.getDateOfBirth());
        }

        if (requestDTO.getAddress() != null) {
            trainee.setAddress(requestDTO.getAddress());
        }

        traineeDao.update(username, trainee);

        log.info("Trainee updated successfully, username={}", username);
    }

    @Override
    public void deleteTrainee(String userId) {

        log.warn("Deleting trainee with userId={}", userId);

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

    @Override
    public boolean matchTrainee(String username, String password) {
        return traineeDao.matchTrainee(username, password);
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
            var trainee = traineeDao.changePassword(username, oldPassword, newPassword);
            if (trainee != null) {
                log.info("Password changed successfully for username={}", username);
                return true;
            }
            log.warn("Failed to change password for username={}, invalid old password", username);
        return false;
    }

    @Override
    public Trainee changeStatus(String username) {

        traineeDao.toggleStatus(username);

        return null;
    }

    @Override
    public void updateTraineeToTrainer(String username, List<String> trainerUsernames) {
        traineeDao.updateTraineeTrainers(username, trainerUsernames);
    }
}