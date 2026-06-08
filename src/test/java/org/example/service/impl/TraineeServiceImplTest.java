package org.example.service.impl;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.exception.AuthenticationException;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TraineeMapperImpl;
import org.example.mapper.TrainerMapperImpl;
import org.example.mapper.TrainingMapperImpl;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.monitoring.metrics.GymCrmMetrics;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private RequestValidator requestValidator;
    @Mock
    private TrainingService trainingService;
    @Mock
    private GymCrmMetrics gymCrmMetrics;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void wireMappers() throws Exception {
        TraineeMapperImpl traineeMapper = new TraineeMapperImpl();
        TrainerMapperImpl trainerMapper = new TrainerMapperImpl();
        Field trainerMapperField = TraineeMapperImpl.class.getDeclaredField("trainerMapper");
        trainerMapperField.setAccessible(true);
        trainerMapperField.set(traineeMapper, trainerMapper);
        setField(traineeService, "traineeMapper", traineeMapper);
        setField(traineeService, "trainerMapper", trainerMapper);
        setField(traineeService, "trainingMapper", new TrainingMapperImpl());
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void createTrainee_generatesCredentials() {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .address("Street 1")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .build();

        when(generator.generateUsername("John", "Smith")).thenReturn("john.smith");
        when(generator.generatePassword()).thenReturn("pwd1234567");
        when(trainerRepository.findByUsername("john.smith")).thenReturn(Optional.empty());
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = traineeService.createTrainee(request);

        assertEquals("john.smith", response.getUserName());
        assertEquals("pwd1234567", response.getPassword());
        verify(requestValidator).validate(request);
        verify(traineeRepository).save(any(Trainee.class));
        verify(gymCrmMetrics).recordTraineeProfileCreated();
    }

    @Test
    void getTrainee_returnsProfile() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        var response = traineeService.getTrainee("john.smith", "pwd");

        assertEquals("John", response.getFirstName());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void getTrainee_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.getTrainee("missing", "pwd"));
    }

    @Test
    void getTrainee_invalidAuth_throws() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authValidator).requireAuthentication("john.smith", "bad");

        assertThrows(AuthenticationException.class, () -> traineeService.getTrainee("john.smith", "bad"));
        verify(traineeRepository, never()).findByUsername(any());
    }

    @Test
    void updateTrainee_updatesFields() {
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        var request = TraineeRequestDTO.builder()
                .firstName("Johnny")
                .lastName("Smith")
                .isActive(false)
                .build();

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var response = traineeService.updateTrainee(request, "john.smith", "pwd");

        assertEquals("Johnny", response.getFirstName());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void deleteTrainee_deletesEntity() {
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        when(traineeRepository.findByUsernameForDelete("john.smith")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john.smith", "pwd");

        verify(authValidator).requireAuthentication("john.smith", "pwd");
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void changeStatus_updatesAndSaves() {
        var trainee = trainee("john.smith", "John", "Smith", false,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var result = traineeService.changeStatus("john.smith", false, "pwd");

        assertSame(trainee, result);
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void updateTraineeTrainers_returnsUpdatedList() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(List.of("trainer1"))).thenReturn(List.of(trainer));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var response = traineeService.updateTraineeTrainers("john.smith", List.of("trainer1"), "pwd");

        assertEquals(1, response.getTrainerList().size());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void getUnassignedTrainers_mapsActiveTrainers() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        when(trainerRepository.findActiveNotAssignedToTrainee("john.smith")).thenReturn(List.of(trainer));

        var result = traineeService.getUnassignedTrainers("john.smith", "pwd");

        assertEquals(1, result.size());
        assertEquals("trainer1", result.get(0).getUserName());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void getTraineeTrainings_returnsMappedTrainings() {
        var trainerUser = User.builder().firstName("Ann").lastName("Lee").build();
        var trainer = Trainer.builder().user(trainerUser).build();
        var training = Training.builder()
                .trainingName("Cardio")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .trainer(trainer)
                .trainingType(TrainingType.builder().trainingTypeName("Fitness").build())
                .build();

        when(trainingService.getAllTrainingsByTraineeUsername(eq("john.smith"), eq("pwd"), any(TrainingCriteria.class)))
                .thenReturn(List.of(training));

        var response = traineeService.getTraineeTrainings("john.smith", "pwd", null, null, null, null);

        assertEquals(1, response.getTrainings().size());
        assertEquals("Cardio", response.getTrainings().get(0).getTrainingName());
    }

    @Test
    void updateTrainee_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());
        var request = TraineeRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .build();

        assertThrows(ResourceNotFoundException.class,
                () -> traineeService.updateTrainee(request, "missing", "pwd"));
    }

    private static Trainee trainee(String username, String firstName, String lastName, boolean isActive,
                                   LocalDate dateOfBirth, String address, List<Trainer> trainers) {
        return Trainee.builder()
                .dateOfBirth(dateOfBirth)
                .address(address)
                .user(User.builder()
                        .userName(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(isActive)
                        .build())
                .trainers(trainers == null ? new ArrayList<>() : new ArrayList<>(trainers))
                .build();
    }

    private static Trainer trainer(String username, String firstName, String lastName, Long specializationId) {
        return Trainer.builder()
                .user(User.builder()
                        .userName(username)
                        .firstName(firstName)
                        .lastName(lastName)
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder()
                        .trainingTypeId(specializationId)
                        .trainingTypeName("Fitness")
                        .build())
                .build();
    }
}
