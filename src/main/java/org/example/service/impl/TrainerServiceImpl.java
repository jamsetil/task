package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.mapper.TrainingMapper;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.example.util.CredentialGenerator;
import org.example.util.JwtUtil;
import org.example.validation.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@Profile("!docker")

public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerRepository trainerRepository;
    @Autowired
    private TraineeRepository traineeRepository;
    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private CredentialGenerator generator;
    @Autowired
    private TrainingService trainingService;
    @Autowired
    private TrainerMapper trainerMapper;
    @Autowired
    private TrainingMapper trainingMapper;
    @Autowired
    private RequestValidator requestValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public TrainerResponseDTO createTrainer(TrainerCreateRequestDTO requestDTO) {
        requestValidator.validate(requestDTO);
        log.info("Creating trainer profile for firstName={}, lastName={}",
                requestDTO.getFirstName(), requestDTO.getLastName());

        String username = generator.generateUsername(requestDTO.getFirstName(), requestDTO.getLastName());
        if (traineeRepository.findByUsername(username).isPresent()) {
            throw new IllegalStateException("user already registered as trainee");
        }
        String password = generator.generatePassword();

        User user = User.builder()
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .userName(username)
                .password(passwordEncoder.encode(password))
                .build();

        var specialization = trainingTypeRepository.findByTrainingTypeName(requestDTO.getSpecializationName())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Training type not found: " + requestDTO.getSpecializationName()));

        Trainer trainer = Trainer.builder()
                .user(user)
                .specialization(specialization)
                .build();

        trainerRepository.save(trainer);

        log.info("Trainer profile created successfully, username={}", username);

        return TrainerResponseDTO.builder()
                .userName(username)
                .password(password)
                .token(jwtUtil.generateToken(username))
                .build();
    }

    @Override
    @Transactional
    public TrainerResponseDTO updateTrainer(TrainerRequestDTO requestDTO, String username) {
        requestValidator.validate(requestDTO);

        log.info("Updating trainer with username={}", username);

        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

        trainer.getUser().setFirstName(requestDTO.getFirstName());
        trainer.getUser().setLastName(requestDTO.getLastName());
        trainer.getUser().setIsActive(requestDTO.getIsActive());

        trainerRepository.save(trainer);
        log.info("Trainer updated successfully, username={}", username);

        return trainerMapper.toResponseDTO(trainer);
    }

    @Override
    public TrainerResponseDTO getTrainer(String username) {
        log.debug("Fetching trainer with username={}", username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));

        return trainerMapper.toResponseDTO(trainer);
    }

    @Override
    @Transactional
    public Trainer toggleTrainerStatus(String username, boolean isActive) {
        log.info("Toggling trainer active status, username={} isActive={}", username, isActive);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with username: " + username));
        trainer.getUser().setIsActive(isActive);
        return trainerRepository.save(trainer);
    }

    @Override
    public TrainerResponseDTO getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                                  String traineeName) {
        TrainingCriteria criteria = TrainingCriteria.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();

        List<org.example.model.Training> trainings =
                trainingService.getAllTrainingsByTrainerUsername(username, criteria);

        return TrainerResponseDTO.builder()
                .trainingResponseDTOList(trainings.stream()
                        .map(trainingMapper::toResponseDTO)
                        .toList())
                .build();
    }
}
