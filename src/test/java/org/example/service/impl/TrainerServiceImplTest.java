package org.example.service.impl;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.exception.AuthenticationException;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.mapper.TrainingMapperImpl;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.monitoring.metrics.GymCrmMetrics;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingService;
import org.example.util.AuthValidator;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private TrainingService trainingService;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private RequestValidator requestValidator;
    @Mock
    private GymCrmMetrics gymCrmMetrics;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void wireTrainingMapper() throws Exception {
        Field field = trainerService.getClass().getDeclaredField("trainingMapper");
        field.setAccessible(true);
        field.set(trainerService, new TrainingMapperImpl());
    }

    @Test
    void createTrainer_generatesCredentialsAndLooksUpTrainingType() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .specializationName("Body Building")
                .build();

        when(generator.generateUsername("Ilyas", "Azizzade")).thenReturn("ilyas.azizzade");
        when(generator.generatePassword()).thenReturn("secret1234");
        when(traineeRepository.findByUsername("ilyas.azizzade")).thenReturn(Optional.empty());
        when(trainingTypeRepository.findByTrainingTypeName("Body Building"))
                .thenReturn(Optional.of(TrainingType.builder().trainingTypeName("Body Building").build()));
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = trainerService.createTrainer(request);

        assertEquals("ilyas.azizzade", response.getUserName());
        verify(requestValidator).validate(request);
        verify(gymCrmMetrics).recordTrainerProfileCreated();
    }

    @Test
    void createTrainer_unknownTrainingType_throws() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .specializationName("Unknown")
                .build();

        when(generator.generateUsername(any(), any())).thenReturn("ilyas.azizzade");
        when(generator.generatePassword()).thenReturn("secret1234");
        when(traineeRepository.findByUsername("ilyas.azizzade")).thenReturn(Optional.empty());
        when(trainingTypeRepository.findByTrainingTypeName("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.createTrainer(request));
    }

    @Test
    void getTrainer_returnsMappedProfile() {
        var trainer = trainer("ilyas.azizzade", "Ilyas", "Azizzade", true);
        var dto = TrainerResponseDTO.builder().userName("ilyas.azizzade").firstName("Ilyas").build();

        when(trainerRepository.findByUsername("ilyas.azizzade")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponseDTO(trainer)).thenReturn(dto);

        var response = trainerService.getTrainer("ilyas.azizzade", "pwd");

        assertEquals("ilyas.azizzade", response.getUserName());
        verify(authValidator).requireAuthentication("ilyas.azizzade", "pwd");
    }

    @Test
    void updateTrainer_updatesFields() {
        var trainer = trainer("ilyas.azizzade", "Ilyas", "Azizzade", true);
        var request = TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Updated")
                .isActive(false)
                .build();
        var dto = TrainerResponseDTO.builder().userName("ilyas.azizzade").lastName("Updated").build();

        when(trainerRepository.findByUsername("ilyas.azizzade")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toResponseDTO(trainer)).thenReturn(dto);

        var response = trainerService.updateTrainer(request, "ilyas.azizzade", "pwd");

        assertEquals("Updated", response.getLastName());
        verify(authValidator).requireAuthentication("ilyas.azizzade", "pwd");
    }

    @Test
    void toggleTrainerStatus_updatesAndSaves() {
        var trainer = trainer("ilyas.azizzade", "Ilyas", "Azizzade", false);
        when(trainerRepository.findByUsername("ilyas.azizzade")).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        var result = trainerService.toggleTrainerStatus("ilyas.azizzade", false, "pwd");

        assertSame(trainer, result);
        verify(authValidator).requireAuthentication("ilyas.azizzade", "pwd");
    }

    @Test
    void getTrainerTrainings_returnsMappedTrainings() {
        var training = Training.builder()
                .trainingName("Cardio")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(45)
                .trainingType(TrainingType.builder().trainingTypeName("Fitness").build())
                .trainee(org.example.model.Trainee.builder()
                        .user(User.builder().firstName("John").lastName("Smith").build())
                        .build())
                .build();
        when(trainingService.getAllTrainingsByTrainerUsername(eq("ilyas.azizzade"), eq("pwd"), any(TrainingCriteria.class)))
                .thenReturn(List.of(training));

        var response = trainerService.getTrainerTrainings("ilyas.azizzade", "pwd", null, null, null);

        assertEquals(1, response.getTrainingResponseDTOList().size());
    }

    @Test
    void getTrainer_invalidAuth_throws() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authValidator).requireAuthentication("ilyas.azizzade", "bad");

        assertThrows(AuthenticationException.class, () -> trainerService.getTrainer("ilyas.azizzade", "bad"));
    }

    @Test
    void updateTrainer_notFound_throws() {
        when(trainerRepository.findByUsername("missing")).thenReturn(Optional.empty());
        var request = TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> trainerService.updateTrainer(request, "missing", "pwd"));
    }

    private static Trainer trainer(String username, String firstName, String lastName, boolean isActive) {
        return Trainer.builder()
                .user(User.builder()
                        .userName(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(isActive)
                        .build())
                .specialization(TrainingType.builder().trainingTypeId(1L).trainingTypeName("Fitness").build())
                .build();
    }
}
