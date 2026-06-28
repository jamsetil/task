package org.example.client;

import org.example.dto.request.TrainerWorkloadRequest;
import org.example.enums.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WorkloadClientFallbackTest {

    @Test
    void fallback_doesNotThrow() {
        WorkloadClient fallback = new WorkloadClientFallback()
                .create(new RuntimeException("connection refused"));

        TrainerWorkloadRequest request = TrainerWorkloadRequest.builder()
                .trainerUsername("trainer.user")
                .actionType(ActionType.ADD)
                .build();

        assertDoesNotThrow(() -> fallback.updateTrainerWorkload("Bearer token", request));
        assertDoesNotThrow(() -> fallback.deleteTrainerWorkload("Bearer token", request));
    }
}
