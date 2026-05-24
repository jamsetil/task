package org.example.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RequestValidator {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public <T> void validate(T request) {
        if (request == null) {
            throw new IllegalArgumentException("Validation failed: request must not be null");
        }
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw validationException(violations);
        }
    }

    private static <T> IllegalArgumentException validationException(Set<ConstraintViolation<T>> violations) {
        String message = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
        return new IllegalArgumentException("Validation failed: " + message);
    }
}
