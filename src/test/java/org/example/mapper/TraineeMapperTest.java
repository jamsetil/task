package org.example.mapper;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() throws Exception {
        TraineeMapperImpl mapper = new TraineeMapperImpl();
        injectTrainerMapper(mapper, new TrainerMapperImpl());
        traineeMapper = mapper;
    }

    private static void injectTrainerMapper(TraineeMapperImpl mapper, TrainerMapper trainerMapper) throws Exception {
        Field field = TraineeMapperImpl.class.getDeclaredField("trainerMapper");
        field.setAccessible(true);
        field.set(mapper, trainerMapper);
    }

    @Test
    void toResponseDTO_mapsTraineeWithTrainers() {
        var trainer = Trainer.builder()
                .user(User.builder()
                        .userName("ilyas.azizzade")
                        .firstName("Ilyas")
                        .lastName("Azizzade")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder().trainingTypeId(2L).trainingTypeName("Fitness").build())
                .build();
        var trainee = Trainee.builder()
                .user(User.builder()
                        .userName("faiq.azizzade")
                        .firstName("Faiq")
                        .lastName("Azizzade")
                        .isActive(true)
                        .build())
                .dateOfBirth(LocalDate.of(1998, 3, 15))
                .address("Baku")
                .trainers(java.util.List.of(trainer))
                .build();

        var dto = traineeMapper.toResponseDTO(trainee);

        assertEquals("faiq.azizzade", dto.getUserName());
        assertEquals("Faiq", dto.getFirstName());
        assertEquals("Azizzade", dto.getLastName());
        assertEquals("1998-03-15", dto.getDateOfBirth());
        assertEquals("Baku", dto.getAddress());
        assertEquals(1, dto.getTrainerList().size());
        assertEquals("ilyas.azizzade", dto.getTrainerList().get(0).getUserName());
    }

    @Test
    void toResponseDTO_nullTrainee() {
        assertNull(traineeMapper.toResponseDTO(null));
    }

    @Test
    void toResponseDTO_nullTrainersList() {
        var trainee = Trainee.builder()
                .user(User.builder()
                        .userName("faiq.azizzade")
                        .firstName("Faiq")
                        .lastName("Azizzade")
                        .isActive(true)
                        .build())
                .build();

        var dto = traineeMapper.toResponseDTO(trainee);

        assertNull(dto.getTrainerList());
    }

    @Test
    void toResponseDTO_nullDateOfBirth() {
        var trainee = Trainee.builder()
                .user(User.builder()
                        .userName("faiq.azizzade")
                        .firstName("Faiq")
                        .lastName("Azizzade")
                        .isActive(true)
                        .build())
                .build();

        var dto = traineeMapper.toResponseDTO(trainee);

        assertNull(dto.getDateOfBirth());
    }
}
