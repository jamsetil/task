package org.example.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.service.TrainerService;
import org.example.util.CredentialGenerator;
import org.example.util.UserProfileUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private UserProfileUpdater profileUpdater;

    @Override
    @Transactional
    public TrainerResponseDTO createTrainer(TrainerCreateRequestDTO requestDTO) {

        log.info("Creating trainer profile");
        Trainer trainer = Trainer.builder()
                .build();

        log.info("creating user profile for trainer with username={}", requestDTO.getUserName());


        User user = User.builder()
                .isActive(requestDTO.getIsActive())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .userName(requestDTO.getUserName())
                .password(generator.generatePassword())
                .build();

        log.info("creating specialization (training type) for trainer with specializationIdOrName={}",
                requestDTO.getSpecializationName());


        TrainingType specialization = TrainingType.builder()
                .trainingTypeName(requestDTO.getSpecializationName())
                .build();

        trainer.setUser(user);
        trainer.setSpecialization(specialization);

        trainerDAO.save(trainer);
        log.info("Trainer profile created successfully, username={}", requestDTO.getUserName());
        return TrainerResponseDTO.builder()
                .userName(trainer.getUser().getUserName())
                .build();
    }

    @Override
    public void updateTrainer(String username, TrainerRequestDTO requestDTO) {
        log.info("Updating trainer with username={}", username);

        Trainer trainer = trainerDAO.find(username)
                .orElseThrow(() -> new RuntimeException("Trainer not found with username: " + username));

        User user = trainer.getUser();

        if (requestDTO.getFirstName() != null) {
            user.setFirstName(requestDTO.getFirstName());
        }

        if (requestDTO.getLastName() != null) {
            user.setLastName(requestDTO.getLastName());
        }

        if (requestDTO.getUserName() != null) {
            user.setUserName(requestDTO.getUserName());
        }

        if (requestDTO.getIsActive() != null) {
            user.setIsActive(requestDTO.getIsActive());
        }


        trainerDAO.update(username ,trainer);

        log.info("Trainer updated successfully, username={}", username);
    }
    @Override
    public Trainer getTrainer(String username) {

        log.debug("Fetching trainer with userId={}", username);
        return trainerDAO.find(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found, userId={}", username);
                    return new RuntimeException("Trainer not found with username: " + username);
                });
    }

    @Override
    public Trainer toggleTrainerStatus(String username) {
        return trainerDAO.toggleStatus(username);
    }

    @Override
    public List<Trainer> getAllTrainersWithNoTrainee(String traineeUsername) {
        return trainerDAO.findTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        return trainerDAO.matchTrainer(username, password);
    }

    @Override
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        var trainer = trainerDAO.find(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for password change, userId={}", username);
                    return new RuntimeException("Trainer not found with username: " + username);
                });
        return trainerDAO.changePassword(trainer.getUser().getUserName(), oldPassword, newPassword);
    }
}