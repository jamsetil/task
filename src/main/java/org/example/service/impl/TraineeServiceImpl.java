package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.mapper.TrainerMapper;
import org.example.mapper.TrainingMapper;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.service.TraineeService;
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
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TraineeMapper traineeMapper;
    @Autowired
    private TrainerMapper trainerMapper;
    @Autowired
    private TrainingMapper trainingMapper;

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
                .password(password)
                .userName(username)
                .build();
    }

    @Override
    public TraineeResponseDTO updateTrainee(TraineeRequestDTO requestDTO, String username, String password) {
        requestValidator.validate(requestDTO);
        authValidator.requireAuthentication(username, password);
        Trainee trainee = traineeDao.find(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));

        log.info("Updating trainee profile for username={}", username);

        trainee.getUser().setFirstName(requestDTO.getFirstName());
        trainee.getUser().setLastName(requestDTO.getLastName());
        trainee.getUser().setIsActive(requestDTO.getIsActive());

        if (requestDTO.getDateOfBirth() != null) {
            trainee.setDateOfBirth(requestDTO.getDateOfBirth());
        }

        if (requestDTO.getAddress() != null) {
            trainee.setAddress(requestDTO.getAddress());
        }

        Trainee updatedTrainee = traineeDao.update(trainee);
        log.info("Trainee updated successfully, username={}", username);

        return traineeMapper.toResponseDTO(updatedTrainee);
    }

    @Override
    public void deleteTrainee(String username, String password) {
        authValidator.requireAuthentication(username, password);
        log.warn("Deleting trainee with username={}", username);
        traineeDao.delete(username);
        log.info("Trainee deleted successfully, username={}", username);
    }

    @Override
    public TraineeResponseDTO getTrainee(String username, String password) {
        authValidator.requireAuthentication(username, password);
        log.debug("Fetching trainee with username={}", username);

        Trainee trainee = traineeDao.find(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));

        return traineeMapper.toResponseDTO(trainee);
    }

    @Override
    @Transactional
    public Trainee changeStatus(String username, boolean isActive, String password) {
        authValidator.requireAuthentication(username, password);
        log.info("Toggling trainee active status, username={} isActive={}", username, isActive);
        return traineeDao.toggleStatus(username, isActive);
    }

    @Override
    @Transactional
    public TraineeResponseDTO updateTraineeTrainers(String username, List<String> trainerUsernames, String password) {
        authValidator.requireAuthentication(username, password);
        requestValidator.validate(TraineeTrainersUpdateRequestDTO.builder()
                .trainerUsernames(trainerUsernames)
                .build());
        log.info("Updating trainer list for trainee username={}", username);
        Trainee trainee = traineeDao.updateTraineeTrainers(username, trainerUsernames);

        return TraineeResponseDTO.builder()
                .trainerList(mapTrainers(trainee.getTrainers()))
                .build();
    }

    @Override
    public List<TrainerResponseDTO> getUnassignedTrainers(String traineeUsername, String password) {
        authValidator.requireAuthentication(traineeUsername, password);
        log.debug("Fetching unassigned active trainers for trainee username={}", traineeUsername);
        return trainerDAO.findTrainersNotAssignedToTrainee(traineeUsername).stream()
                .map(trainerMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TraineeResponseDTO getTraineeTrainings(String username, String password,
                                                  LocalDate fromDate, LocalDate toDate,
                                                  String trainerName, String trainingType) {
        TrainingCriteria criteria = TrainingCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingType(trainingType)
                .build();

        var trainings = trainingService.getAllTrainingsByTraineeUsername(username, password, criteria);

        return TraineeResponseDTO.builder()
                .trainings(trainings.stream().map(trainingMapper::toResponseDTO).toList())
                .build();
    }

    private List<TrainerResponseDTO> mapTrainers(List<Trainer> trainers) {
        if (trainers == null) {
            return List.of();
        }
        return trainers.stream()
                .map(trainerMapper::toResponseDTO)
                .toList();
    }
}
