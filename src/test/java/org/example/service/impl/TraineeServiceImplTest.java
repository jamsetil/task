package org.example.service.impl;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private RequestValidator requestValidator;
    @Mock
    private TrainingService trainingService;

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
        doAnswer(invocation -> null).when(traineeDAO).save(any(Trainee.class));

        var response = traineeService.createTrainee(request);

        assertEquals("john.smith", response.getUserName());
        assertEquals("pwd1234567", response.getPassword());
        verify(requestValidator).validate(request);
        verify(traineeDAO).save(any(Trainee.class));
    }



    @Test
    void getTrainee_returnsProfile() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeDAO.find("john.smith")).thenReturn(java.util.Optional.of(trainee));

        var response = traineeService.getTrainee("john.smith", "pwd");

        assertEquals("John", response.getFirstName());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void getTrainee_notFound_throws() {
        when(traineeDAO.find("missing")).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> traineeService.getTrainee("missing", "pwd"));
    }

    @Test
    void getTrainee_invalidAuth_throws() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authValidator).requireAuthentication("john.smith", "bad");

        assertThrows(AuthenticationException.class, () -> traineeService.getTrainee("john.smith", "bad"));
        verify(traineeDAO, never()).find(any());
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

        when(traineeDAO.find("john.smith")).thenReturn(java.util.Optional.of(trainee));
        when(traineeDAO.update(trainee)).thenReturn(trainee);

        var response = traineeService.updateTrainee(request, "john.smith", "pwd");

        assertEquals("Johnny", response.getFirstName());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void deleteTrainee_delegatesToDao() {
        traineeService.deleteTrainee("john.smith", "pwd");

        verify(authValidator).requireAuthentication("john.smith", "pwd");
        verify(traineeDAO).delete("john.smith");
    }

    @Test
    void changeStatus_delegatesToDao() {
        var trainee = trainee("john.smith", "John", "Smith", false,
                LocalDate.of(1995, 5, 20), "Street 1", List.of());
        when(traineeDAO.toggleStatus("john.smith", false)).thenReturn(trainee);

        var result = traineeService.changeStatus("john.smith", false, "pwd");

        assertSame(trainee, result);
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void updateTraineeTrainers_returnsUpdatedList() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        var trainee = trainee("john.smith", "John", "Smith", true,
                LocalDate.of(1995, 5, 20), "Street 1", List.of(trainer));

        when(traineeDAO.updateTraineeTrainers("john.smith", List.of("trainer1"))).thenReturn(trainee);

        var response = traineeService.updateTraineeTrainers("john.smith", List.of("trainer1"), "pwd");

        assertEquals(1, response.getTrainerList().size());
        verify(authValidator).requireAuthentication("john.smith", "pwd");
    }

    @Test
    void getUnassignedTrainers_mapsActiveTrainers() {
        var trainer = trainer("trainer1", "Ann", "Lee", 3L);
        when(trainerDAO.findTrainersNotAssignedToTrainee("john.smith")).thenReturn(List.of(trainer));

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
        when(traineeDAO.find("missing")).thenReturn(java.util.Optional.empty());
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
                .trainers(trainers)
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
