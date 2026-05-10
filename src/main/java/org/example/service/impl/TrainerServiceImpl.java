package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDAO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainer;
import org.example.service.TrainerService;
import org.example.util.CredentialGenerator;
import org.example.util.UserProfileUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public TrainerResponseDTO createTrainer(TrainerRequestDTO requestDTO) {

        log.info("Creating trainer profile");

        Trainer trainer = Trainer.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .isActive(requestDTO.getIsActive())
                .specialization(requestDTO.getSpecialization())
                .userId(UUID.randomUUID().toString())
                .trainings(requestDTO.getTrainings())
                .trainingTypes(requestDTO.getTrainingTypes())
                .build();

        trainer.setUserName(generator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName()
        ));

        trainer.setPassword(generator.generatePassword());

        trainerDAO.save(trainer);

        log.info("Trainer created successfully with userId={}", trainer.getUserId());

        return TrainerResponseDTO.builder()
                .lastName(trainer.getLastName())
                .isActive(trainer.getIsActive())
                .userName(trainer.getUserName())
                .firstName(trainer.getFirstName())
                .specialization(trainer.getSpecialization())
                .userId(trainer.getUserId())
                .trainings(trainer.getTrainings())
                .build();
    }

    @Override
    public void updateTrainer(String userId, TrainerRequestDTO requestDTO) {

        log.info("Updating trainer with userId={}", userId);

        Trainer entity = trainerDAO.find(userId)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update, userId={}", userId);
                    return new ResourceNotFoundException("Trainer not found with id: " + userId);
                });

        profileUpdater.updateUserProfile(requestDTO, entity);

        entity.setIsActive(requestDTO.getIsActive());
        entity.setSpecialization(requestDTO.getSpecialization());
        entity.setTrainings(requestDTO.getTrainings());
        entity.setTrainingTypes(requestDTO.getTrainingTypes());

        trainerDAO.update(userId, entity);

        log.info("Trainer updated successfully, userId={}", userId);
    }

    @Override
    public TrainerResponseDTO getTrainer(String userId) {

        log.debug("Fetching trainer with userId={}", userId);

        var trainer = trainerDAO.find(userId)
                .orElseThrow(() -> {
                    log.error("Trainer not found, userId={}", userId);
                    return new ResourceNotFoundException("Trainer not found with username: " + userId);
                });

        log.info("Trainer fetched successfully, userId={}", userId);

        return TrainerResponseDTO.builder()
                .userId(trainer.getUserId())
                .isActive(trainer.getIsActive())
                .lastName(trainer.getLastName())
                .trainings(trainer.getTrainings())
                .trainingTypes(trainer.getTrainingTypes())
                .specialization(trainer.getSpecialization())
                .firstName(trainer.getFirstName())
                .userName(trainer.getUserName())
                .build();
    }
}