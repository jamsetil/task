package org.example.validation;

import jakarta.validation.constraints.NotBlank;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestValidatorTest {

    private final RequestValidator validator = new RequestValidator();

    static class SampleRequest {
        @NotBlank
        String name;
    }

    @Test
    void validate_validRequest_passes() {
        SampleRequest request = new SampleRequest();
        request.name = "value";
        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    void validate_invalidRequest_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(new SampleRequest()));
    }

    @Test
    void validate_nullRequest_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    @Test
    void validateUpdate_blankFirstName_throws() {
        var request = TrainerRequestDTO.builder().firstName("   ").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void validateUpdate_nullFields_passes() {
        assertDoesNotThrow(() -> validator.validate(TrainerRequestDTO.builder().build()));
    }

    @Test
    void validateTraineeUpdate_blankAddress_throws() {
        var request = TraineeRequestDTO.builder().address("").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void validateTraineeUpdate_futureDateOfBirth_throws() {
        var request = TraineeRequestDTO.builder().dateOfBirth(LocalDate.now().plusDays(1)).build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void validateLogin_blankUsername_throws() {
        var request = LoginRequestDTO.builder().username(" ").password("pwd").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void validateTrainerUsernames_nullList_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(TraineeTrainersUpdateRequestDTO.builder().trainerUsernames(null).build()));
    }

    @Test
    void validateTrainerUsernames_blankEntry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validate(TraineeTrainersUpdateRequestDTO.builder()
                        .trainerUsernames(List.of("trainer1", "  "))
                        .build()));
    }

    @Test
    void validateTrainerUsernames_validList_passes() {
        assertDoesNotThrow(() -> validator.validate(TraineeTrainersUpdateRequestDTO.builder()
                .trainerUsernames(List.of("trainer1", "trainer2"))
                .build()));
    }

    @Test
    void validateTraining_futureDate_throws() {
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john")
                .trainerUsername("trainer")
                .trainingName("Cardio")
                .trainingTypeName("Cardio")
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDuration(30)
                .build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }

    @Test
    void validateTrainingCriteria_blankTrainerName_throws() {
        var criteria = TrainingCriteria.builder().trainerName("  ").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(criteria));
    }

    @Test
    void validateTraineeCreate_futureDateOfBirth_throws() {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .dateOfBirth(LocalDate.now().plusDays(1))
                .build();
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));
    }
}
