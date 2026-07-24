package org.example.service.impl;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.enums.ActionType;
import org.example.exception.ResourceNotFoundException;
import org.example.mapper.TraineeMapperImpl;
import org.example.mapper.TrainerMapperImpl;
import org.example.mapper.TrainingMapperImpl;
import org.example.messaging.WorkloadMessagePublisher;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.service.TrainingService;
import org.example.util.CredentialGenerator;
import org.example.util.JwtUtil;
import org.example.validation.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
    private RequestValidator requestValidator;
    @Mock
    private TrainingService trainingService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private WorkloadMessagePublisher workloadMessagePublisher;

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
        when(passwordEncoder.encode("pwd1234567")).thenReturn("encoded");
        when(jwtUtil.generateToken("john.smith")).thenReturn("jwt-token");
        when(trainerRepository.findByUsername("john.smith")).thenReturn(Optional.empty());
        when(traineeRepository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = traineeService.createTrainee(request);

        assertEquals("john.smith", response.getUserName());
        assertEquals("pwd1234567", response.getPassword());
        assertEquals("jwt-token", response.getToken());
        verify(requestValidator).validate(request);
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void createTrainee_existingTrainerUsername_throws() {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .build();

        when(generator.generateUsername("John", "Smith")).thenReturn("john.smith");
        when(trainerRepository.findByUsername("john.smith")).thenReturn(Optional.of(
                Trainer.builder().user(User.builder().userName("john.smith").build()).build()));

        assertThrows(IllegalStateException.class, () -> traineeService.createTrainee(request));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void updateTrainee_updatesOptionalFields() {
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        var request = TraineeRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1996, 1, 1))
                .address("New Street")
                .build();

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var response = traineeService.updateTrainee(request, "john.smith");

        assertEquals("New Street", response.getAddress());
        assertEquals("1996-01-01", response.getDateOfBirth());
    }

    @Test
    void deleteTrainee_notFound_throws() {
        when(traineeRepository.findByUsernameForDelete("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.deleteTrainee("missing"));
    }

    @Test
    void changeStatus_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.changeStatus("missing", true));
    }

    @Test
    void updateTraineeTrainers_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> traineeService.updateTraineeTrainers("missing", List.of("trainer1")));
    }

    @Test
    void getTrainee_returnsProfile() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));

        var response = traineeService.getTrainee("john.smith");

        assertEquals("John", response.getFirstName());
    }

    @Test
    void getTrainee_notFound_throws() {
        when(traineeRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.getTrainee("missing"));
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

        var response = traineeService.updateTrainee(request, "john.smith");

        assertEquals("Johnny", response.getFirstName());
    }

    @Test
    void deleteTrainee_deletesEntityAndNotifiesWorkloadService() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var training = Training.builder()
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .trainer(trainer)
                .build();
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        trainee.setTrainings(new ArrayList<>(List.of(training)));

        when(traineeRepository.findByUsernameForDelete("john.smith")).thenReturn(Optional.of(trainee));

        traineeService.deleteTrainee("john.smith");

        verify(workloadMessagePublisher).publish(argThat(request ->
                ActionType.DELETE.equals(request.getActionType())
                        && "trainer1".equals(request.getTrainerUsername())
                        && Integer.valueOf(60).equals(request.getTrainingDuration())));
        verify(traineeRepository).delete(trainee);
    }

    @Test
    void changeStatus_updatesAndSaves() {
        var trainee = trainee("john.smith", "John", "Smith", false,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var result = traineeService.changeStatus("john.smith", false);

        assertSame(trainee, result);
    }

    @Test
    void updateTraineeTrainers_returnsUpdatedList() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeRepository.findByUsername("john.smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUserUserNameIn(List.of("trainer1"))).thenReturn(List.of(trainer));
        when(traineeRepository.save(trainee)).thenReturn(trainee);

        var response = traineeService.updateTraineeTrainers("john.smith", List.of("trainer1"));

        assertEquals(1, response.getTrainerList().size());
    }

    @Test
    void getUnassignedTrainers_mapsActiveTrainers() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        when(trainerRepository.findActiveNotAssignedToTrainee("john.smith")).thenReturn(List.of(trainer));

        var result = traineeService.getUnassignedTrainers("john.smith");

        assertEquals(1, result.size());
        assertEquals("trainer1", result.get(0).getUserName());
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

        when(trainingService.getAllTrainingsByTraineeUsername(eq("john.smith"), any(TrainingCriteria.class)))
                .thenReturn(List.of(training));

        var response = traineeService.getTraineeTrainings("john.smith", null, null, null, null);

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
                () -> traineeService.updateTrainee(request, "missing"));
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
