package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.security.AuthValidator;
import org.example.service.TraineeService;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeDAO traineeDao;
    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private RequestValidator requestValidator;

    @Override
    @Transactional
    public TraineeResponseDTO createTrainee(TraineeCreateRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);

        log.info("Creating trainee profile for firstName={}, lastName={}",
                requestDTO.getFirstName(), requestDTO.getLastName());

        String username = generator.generateUsername(requestDTO.getFirstName(), requestDTO.getLastName());
        String password = generator.generatePassword();

        User user = User.builder()
                .userName(username)
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .isActive(requestDTO.getIsActive())
                .password(password)
                .build();

        Trainee trainee = Trainee.builder()
                .address(requestDTO.getAddress())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .user(user)
                .build();

        traineeDao.save(trainee);
        log.info("Trainee created successfully with username={}", username);

        return TraineeResponseDTO.builder()
                .userName(username)
                .password(password)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .userId(user.getUserId())
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().toString() : null)
                .build();
    }

    @Override
    @Transactional
    public void updateTrainee(LoginRequestDTO auth, String username, TraineeRequestDTO requestDTO) {
        requestValidator.validateTraineeUpdate(requestDTO);
        authValidator.requireTrainee(auth, username);
        log.info("Updating trainee with username={}", username);

        Trainee trainee = traineeDao.find(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));

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
    @Transactional
    public void deleteTrainee(LoginRequestDTO auth, String username) {
        authValidator.requireTrainee(auth, username);
        log.warn("Deleting trainee with username={}", username);
        traineeDao.delete(username);
        log.info("Trainee deleted successfully, username={}", username);
    }

    @Override
    public Trainee getTrainee(LoginRequestDTO auth, String username) {
        authValidator.requireTrainee(auth, username);
        log.debug("Fetching trainee with username={}", username);
        return traineeDao.find(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));
    }

    @Override
    public boolean matchTrainee(String username, String password) {
        return traineeDao.matchTrainee(username, password);
    }

    @Override
    @Transactional
    public boolean changePassword(LoginRequestDTO auth, String oldPassword, String newPassword) {
        authValidator.requireTrainee(auth, auth.getUsername());
        Trainee trainee = traineeDao.changePassword(auth.getUsername(), oldPassword, newPassword);
        if (trainee != null) {
            log.info("Password changed successfully for username={}", auth.getUsername());
            return true;
        }
        log.warn("Failed to change password for username={}, invalid old password", auth.getUsername());
        return false;
    }

    @Override
    @Transactional
    public Trainee changeStatus(LoginRequestDTO auth, String username) {
        authValidator.requireTrainee(auth, username);
        log.info("Toggling trainee active status, username={}", username);
        return traineeDao.toggleStatus(username);
    }

    @Override
    @Transactional
    public void updateTraineeTrainers(LoginRequestDTO auth, String username, List<String> trainerUsernames) {
        requestValidator.validateTrainerUsernames(trainerUsernames);
        authValidator.requireTrainee(auth, username);
        log.info("Updating trainer list for trainee username={}", username);
        traineeDao.updateTraineeTrainers(username, trainerUsernames);
    }

    @Override
    public List<Trainer> getUnassignedTrainers(LoginRequestDTO auth, String traineeUsername) {
        authValidator.requireTrainee(auth, traineeUsername);
        log.debug("Fetching unassigned trainers for trainee username={}", traineeUsername);
        return trainerDAO.findTrainersNotAssignedToTrainee(traineeUsername);
    }
}
