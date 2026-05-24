package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingTypeDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.security.AuthValidator;
import org.example.service.TrainerService;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private RequestValidator requestValidator;

    @Override
    @Transactional
    public TrainerResponseDTO createTrainer(TrainerCreateRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);

        log.info("Creating trainer profile for firstName={}, lastName={}",
                requestDTO.getFirstName(), requestDTO.getLastName());

        String username = generator.generateUsername(requestDTO.getFirstName(), requestDTO.getLastName());
        String password = generator.generatePassword();

        User user = User.builder()
                .isActive(requestDTO.getIsActive())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .userName(username)
                .password(password)
                .build();

        var specialization = trainingTypeDAO.findByName(requestDTO.getSpecializationName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training type not found: " + requestDTO.getSpecializationName()));

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        trainerDAO.save(trainer);
        log.info("Trainer profile created successfully, username={}", username);

        return TrainerResponseDTO.builder()
                .userName(username)
                .password(password)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .userId(user.getUserId())
                .specialization(specialization.getTrainingTypeName())
                .build();
    }

    @Override
    @Transactional
    public void updateTrainer(LoginRequestDTO auth, String username, TrainerRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);
        authValidator.requireTrainer(auth, username);
        log.info("Updating trainer with username={}", username);

        Trainer trainer = trainerDAO.find(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

        User user = trainer.getUser();

        if (requestDTO.getFirstName() != null) {
            user.setFirstName(requestDTO.getFirstName());
        }
        if (requestDTO.getLastName() != null) {
            user.setLastName(requestDTO.getLastName());
        }
        if (requestDTO.getUsername() != null) {
            user.setUserName(requestDTO.getUsername());
        }
        if (requestDTO.getIsActive() != null) {
            user.setIsActive(requestDTO.getIsActive());
        }

        trainerDAO.update(username, trainer);
        log.info("Trainer updated successfully, username={}", username);
    }

    @Override
    public Trainer getTrainer(LoginRequestDTO auth, String username) {
        authValidator.requireTrainer(auth, username);
        log.debug("Fetching trainer with username={}", username);
        return trainerDAO.find(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
    }

    @Override
    @Transactional
    public Trainer toggleTrainerStatus(LoginRequestDTO auth, String username) {
        authValidator.requireTrainer(auth, username);
        log.info("Toggling trainer active status, username={}", username);
        return trainerDAO.toggleStatus(username);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        requestValidator.validate(LoginRequestDTO.builder().username(username).password(password).build());
        return trainerDAO.matchTrainer(username, password);
    }

    @Override
    @Transactional
    public boolean changePassword(LoginRequestDTO auth, String oldPassword, String newPassword) {
        authValidator.requireTrainer(auth, auth.getUsername());
        boolean changed = trainerDAO.changePassword(auth.getUsername(), oldPassword, newPassword);
        if (changed) {
            log.info("Password changed successfully for username={}", auth.getUsername());
        } else {
            log.warn("Failed to change password for username={}, invalid old password", auth.getUsername());
        }
        return changed;
    }
}
