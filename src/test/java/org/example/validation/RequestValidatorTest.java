package org.example.validation;

import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;

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
}
