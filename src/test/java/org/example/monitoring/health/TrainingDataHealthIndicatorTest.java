package org.example.monitoring.health;

import org.example.repository.TrainingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDataHealthIndicatorTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingDataHealthIndicator healthIndicator;

    @Test
    void health_returnsUpWithTrainingCount() {
        when(trainingRepository.count()).thenReturn(12L);

        var health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(12L, health.getDetails().get("trainingCount"));
    }

    @Test
    void health_returnsDownWhenRepositoryFails() {
        doThrow(new RuntimeException("db unavailable")).when(trainingRepository).count();

        var health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
