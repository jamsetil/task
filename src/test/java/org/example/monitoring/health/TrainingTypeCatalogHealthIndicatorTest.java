package org.example.monitoring.health;

import org.example.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeCatalogHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeCatalogHealthIndicator healthIndicator;

    @Test
    void health_returnsUpWhenCatalogIsComplete() {
        when(trainingTypeRepository.count()).thenReturn(5L);

        var health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("trainingTypeCount"));
    }

    @Test
    void health_returnsDownWhenCatalogIsIncomplete() {
        when(trainingTypeRepository.count()).thenReturn(2L);

        var health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals(2L, health.getDetails().get("trainingTypeCount"));
    }
}
