package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.client.WorkloadClient;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.enums.ActionType;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.mapper.TrainerMapper;
import org.example.mapper.TrainingMapper;
import org.example.model.Trainee;
import org.example.model.Training;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.service.TraineeService;
import org.example.service.TrainingService;
import org.example.util.CredentialGenerator;
import org.example.util.JwtUtil;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private CredentialGenerator generator;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private WorkloadClient workloadClient;

    @Override
    @Transactional
    public TraineeResponseDTO createTrainee(TraineeCreateRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);

        log.info("Creating trainee profile for firstName={}, lastName={}",
                requestDTO.getFirstName(), requestDTO.getLastName());

        String username = generator.generateUsername(requestDTO.getFirstName(), requestDTO.getLastName());
        if (trainerRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("user already registered as trainer");
        }
        String password = generator.generatePassword();

        User user = User.builder()
                .userName(username)
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .password(passwordEncoder.encode(password))
                .build();

        Trainee trainee = Trainee.builder()
                .address(requestDTO.getAddress())
                .dateOfBirth(requestDTO.getDateOfBirth())
                .user(user)
                .build();
        traineeRepository.save(trainee);
        log.info("Trainee created successfully with username={}", username);

        return TraineeResponseDTO.builder()
                .password(password)
                .userName(username)
                .token(jwtUtil.generateToken(username))
                .build();
    }

    @Override
    @Transactional
    public TraineeResponseDTO updateTrainee(TraineeRequestDTO requestDTO, String username) {
        requestValidator.validate(requestDTO);
        Trainee trainee = traineeRepository.findByUsername(username)
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

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("Trainee updated successfully, username={}", username);

        return traineeMapper.toResponseDTO(updatedTrainee);
    }

    @Override
    @Transactional
    public void deleteTrainee(String username, String authorizationHeader) {
        log.warn("Deleting trainee with username={}", username);
        Trainee trainee = traineeRepository.findByUsernameForDelete(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));

        if (trainee.getTrainings() != null) {
            trainee.getTrainings().forEach(training ->
                    workloadClient.deleteTrainerWorkload(
                            authorizationHeader, toWorkloadDeleteRequest(training)));
        }

        trainee.getTrainers().clear();
        traineeRepository.delete(trainee);
        log.info("Trainee deleted successfully, username={}", username);
    }

    private TrainerWorkloadRequest toWorkloadDeleteRequest(Training training) {
        var trainer = training.getTrainer();
        var user = trainer.getUser();
        return TrainerWorkloadRequest.builder()
                .actionType(ActionType.DELETE)
                .trainerUsername(user.getUserName())
                .trainerFirstName(user.getFirstName())
                .trainerLastName(user.getLastName())
                .isActive(user.getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }

    @Override
    public TraineeResponseDTO getTrainee(String username) {
        log.debug("Fetching trainee with username={}", username);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));

        return traineeMapper.toResponseDTO(trainee);
    }

    @Override
    @Transactional
    public Trainee changeStatus(String username, boolean isActive) {
        log.info("Toggling trainee active status, username={} isActive={}", username, isActive);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));
        trainee.getUser().setIsActive(isActive);
        return traineeRepository.save(trainee);
    }

    @Override
    @Transactional
    public TraineeResponseDTO updateTraineeTrainers(String username, List<String> trainerUsernames) {
        requestValidator.validate(TraineeTrainersUpdateRequestDTO.builder()
                .trainerUsernames(trainerUsernames)
                .build());
        log.info("Updating trainer list for trainee username={}", username);
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trainee not found with username: " + username));
        List<Trainer> trainers = trainerRepository.findByUserUserNameIn(trainerUsernames);
        trainee.setTrainers(trainers);
        trainee = traineeRepository.save(trainee);

        return TraineeResponseDTO.builder()
                .trainerList(mapTrainers(trainee.getTrainers()))
                .build();
    }

    @Override
    public List<TrainerResponseDTO> getUnassignedTrainers(String traineeUsername) {
        log.debug("Fetching unassigned active trainers for trainee username={}", traineeUsername);
        return trainerRepository.findActiveNotAssignedToTrainee(traineeUsername).stream()
                .map(trainerMapper::toResponseDTO)
                .toList();
    }

    @Override
    public TraineeResponseDTO getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                                  String trainerName, String trainingType) {
        TrainingCriteria criteria = TrainingCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingType(trainingType)
                .build();

        var trainings = trainingService.getAllTrainingsByTraineeUsername(username, criteria);

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
