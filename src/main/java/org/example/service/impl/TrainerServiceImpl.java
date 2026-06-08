package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dao.TrainingTypeDAO;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.mapper.TrainingMapper;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.base.User;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.util.AuthValidator;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerDAO trainerDAO;
    @Autowired
    private TraineeDAO traineeDAO;
    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private AuthValidator authValidator;
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TrainerMapper trainerMapper;
    @Autowired
    private TrainingMapper trainingMapper;
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
                .build();
    }

    @Override
    public TrainerResponseDTO updateTrainer(TrainerRequestDTO requestDTO, String username, String password) {
        requestValidator.validate(requestDTO);
        authValidator.requireAuthentication(username, password);

        log.info("Updating trainer with username={}", username);

        Trainer trainer = trainerDAO.find(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

        trainer.getUser().setFirstName(requestDTO.getFirstName());
        trainer.getUser().setLastName(requestDTO.getLastName());
        trainer.getUser().setIsActive(requestDTO.getIsActive());

        trainerDAO.update(trainer);
        log.info("Trainer updated successfully, username={}", username);

        return trainerMapper.toResponseDTO(trainer);
    }

    @Override
    public TrainerResponseDTO getTrainer(String username, String password) {
        authValidator.requireAuthentication(username, password);
        log.debug("Fetching trainer with username={}", username);
        Trainer trainer = trainerDAO.find(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

        return trainerMapper.toResponseDTO(trainer);
    }

    @Override
    @Transactional
    public Trainer toggleTrainerStatus(String username, boolean isActive, String password) {
        authValidator.requireAuthentication(username, password);
        log.info("Toggling trainer active status, username={} isActive={}", username, isActive);
        return trainerDAO.toggleStatus(username, isActive);
    }

    @Override
    public TrainerResponseDTO getTrainerTrainings(String username, String password,
                                                  LocalDate fromDate, LocalDate toDate,
                                                  String traineeName) {
        TrainingCriteria criteria = TrainingCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();

        List<Training> trainings = trainingService.getAllTrainingsByTrainerUsername(username, password, criteria);

        return TrainerResponseDTO.builder()
                .trainingResponseDTOList(trainings.stream()
                        .map(trainingMapper::toResponseDTO)
                        .toList())
                .build();
    }
}
