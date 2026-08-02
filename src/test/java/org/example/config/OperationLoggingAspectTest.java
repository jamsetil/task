package org.example.config;

import org.example.model.Training;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperationLoggingAspectTest {

    @Test
    void formatArgs_sanitizesBearerToken() {
        assertEquals("[Bearer ***]", OperationLoggingAspect.formatArgs(new Object[]{"Bearer secret-token"}));
    }

    @Test
    void summarizeResult_formatsCollectionAndOptional() {
        assertEquals("collection(size=2)", OperationLoggingAspect.summarizeResult(List.of("a", "b")));
        assertEquals("empty", OperationLoggingAspect.summarizeResult(Optional.empty()));
        assertTrue(OperationLoggingAspect.summarizeResult(Optional.of("value")).contains("value"));
    }

    @Test
    void summarizeResult_avoidsEntityToString() {
        assertEquals("Training", OperationLoggingAspect.summarizeResult(Training.builder().build()));
    }
}
