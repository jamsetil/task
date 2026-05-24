package org.example.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.base.BaseUpdateRequestDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RequestValidator {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public <T> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw validationException(violations);
        }
    }

    public void validateUpdate(BaseUpdateRequestDTO request) {
        List<String> errors = new ArrayList<>();
        rejectIfBlank(request.getFirstName(), "firstName", errors);
        rejectIfBlank(request.getLastName(), "lastName", errors);
        rejectIfBlank(request.getUsername(), "username", errors);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }

    public void validateTraineeUpdate(TraineeRequestDTO request) {
        validateUpdate(request);
        List<String> errors = new ArrayList<>();
        rejectIfBlank(request.getAddress(), "address", errors);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }

    public void validateTrainerUsernames(List<String> trainerUsernames) {
        if (trainerUsernames == null) {
            throw new IllegalArgumentException("Validation failed: trainerUsernames: must not be null");
        }
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < trainerUsernames.size(); i++) {
            String username = trainerUsernames.get(i);
            if (username == null || username.isBlank()) {
                errors.add("trainerUsernames[" + i + "]: must not be blank");
            }
        }
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
        }
    }

    private static void rejectIfBlank(String value, String field, List<String> errors) {
        if (value != null && value.isBlank()) {
            errors.add(field + ": must not be blank");
        }
    }

    private static <T> IllegalArgumentException validationException(Set<ConstraintViolation<T>> violations) {
        String message = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return new IllegalArgumentException("Validation failed: " + message);
    }
}
