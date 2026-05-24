package org.example.validation;

import jakarta.validation.constraints.NotBlank;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.junit.jupiter.api.Test;

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
    void validateUpdate_blankFirstName_throws() {
        var request = TrainerRequestDTO.builder().firstName("   ").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validateUpdate(request));
    }

    @Test
    void validateUpdate_nullFields_passes() {
        assertDoesNotThrow(() -> validator.validateUpdate(TrainerRequestDTO.builder().build()));
    }

    @Test
    void validateTraineeUpdate_blankAddress_throws() {
        var request = TraineeRequestDTO.builder().address("").build();
        assertThrows(IllegalArgumentException.class, () -> validator.validateTraineeUpdate(request));
    }

    @Test
    void validateTrainerUsernames_nullList_throws() {
        assertThrows(IllegalArgumentException.class, () -> validator.validateTrainerUsernames(null));
    }

    @Test
    void validateTrainerUsernames_blankEntry_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> validator.validateTrainerUsernames(List.of("trainer1", "  ")));
    }

    @Test
    void validateTrainerUsernames_validList_passes() {
        assertDoesNotThrow(() -> validator.validateTrainerUsernames(List.of("trainer1", "trainer2")));
    }
}
