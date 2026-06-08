package org.example.mapper;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainingMapperTest {

    private final TrainingMapper mapper = new TrainingMapperImpl();

    @Test
    void toResponseDTO_mapsTrainingFields() {
        var training = Training.builder()
                .trainingName("Cardio")
                .trainingDate(LocalDate.of(2024, 6, 1))
                .trainingDuration(60)
                .trainer(Trainer.builder()
                        .user(User.builder().firstName("Ilyas").lastName("Azizzade").build())
                        .build())
                .trainee(Trainee.builder()
                        .user(User.builder().firstName("Faiq").lastName("Azizzade").build())
                        .build())
                .trainingType(TrainingType.builder().trainingTypeName("Fitness").build())
                .build();

        var dto = mapper.toResponseDTO(training);

        assertEquals("Cardio", dto.getTrainingName());
        assertEquals("2024-06-01", dto.getTrainingDate());
        assertEquals(60, dto.getTrainingDuration());
        assertEquals("Ilyas Azizzade", dto.getTrainerName());
        assertEquals("Faiq Azizzade", dto.getTraineeName());
        assertEquals("Fitness", dto.getTrainingType());
    }

    @Test
    void toResponseDTO_nullTrainingType() {
        var training = Training.builder()
                .trainingName("Stretch")
                .trainingDate(LocalDate.of(2024, 7, 1))
                .trainingDuration(30)
                .build();

        var dto = mapper.toResponseDTO(training);

        assertNull(dto.getTrainingType());
        assertNull(dto.getTrainerName());
        assertNull(dto.getTraineeName());
    }
}
