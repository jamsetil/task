package org.example.mapper;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TrainerMapperTest {

    private final TrainerMapper mapper = new TrainerMapperImpl();

    @Test
    void toResponseDTO_mapsTrainerAndTrainees() {
        var trainee = Trainee.builder()
                .user(User.builder()
                        .userName("john.smith")
                        .firstName("John")
                        .lastName("Smith")
                        .isActive(true)
                        .build())
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .address("Street 1")
                .build();
        var trainer = Trainer.builder()
                .user(User.builder()
                        .userName("ilyas.azizzade")
                        .firstName("Ilyas")
                        .lastName("Azizzade")
                        .isActive(true)
                        .build())
                .specialization(TrainingType.builder().trainingTypeId(2L).trainingTypeName("Fitness").build())
                .trainees(List.of(trainee))
                .build();

        var dto = mapper.toResponseDTO(trainer);

        assertEquals("ilyas.azizzade", dto.getUserName());
        assertEquals("Ilyas", dto.getFirstName());
        assertEquals("Azizzade", dto.getLastName());
        assertEquals(true, dto.getIsActive());
        assertEquals(2L, dto.getSpecializationId());
        assertEquals(1, dto.getTraineeResponseDTOList().size());
        assertEquals("john.smith", dto.getTraineeResponseDTOList().get(0).getUserName());
        assertEquals("1995-05-20", dto.getTraineeResponseDTOList().get(0).getDateOfBirth());
    }

    @Test
    void toTraineeDto_mapsTraineeFields() {
        var trainee = Trainee.builder()
                .user(User.builder()
                        .userName("john.smith")
                        .firstName("John")
                        .lastName("Smith")
                        .isActive(false)
                        .build())
                .dateOfBirth(null)
                .address("Street 1")
                .build();

        var dto = mapper.toTraineeDto(trainee);

        assertEquals("john.smith", dto.getUserName());
        assertEquals("John", dto.getFirstName());
        assertEquals("Street 1", dto.getAddress());
        assertNull(dto.getDateOfBirth());
        assertEquals(false, dto.getIsActive());
    }
}
