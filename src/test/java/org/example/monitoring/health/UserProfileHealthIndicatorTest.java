package org.example.monitoring.health;

import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
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
class UserProfileHealthIndicatorTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private UserProfileHealthIndicator healthIndicator;

    @Test
    void health_returnsUpWithCounts() {
        when(traineeRepository.count()).thenReturn(4L);
        when(trainerRepository.count()).thenReturn(2L);

        var health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(4L, health.getDetails().get("traineeCount"));
        assertEquals(2L, health.getDetails().get("trainerCount"));
    }

    @Test
    void health_returnsDownWhenRepositoryFails() {
        doThrow(new RuntimeException("db unavailable")).when(traineeRepository).count();

        var health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
