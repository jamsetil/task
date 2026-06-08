package org.example.monitoring.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.TrainingTypeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("trainingTypeCatalog")
@RequiredArgsConstructor
public class TrainingTypeCatalogHealthIndicator implements HealthIndicator {

    static final long MINIMUM_TRAINING_TYPES = 5;

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        long count = trainingTypeRepository.count();
        log.debug("Training type catalog health check, count={}", count);

        if (count >= MINIMUM_TRAINING_TYPES) {
            return Health.up()
                    .withDetail("trainingTypeCount", count)
                    .withDetail("minimumRequired", MINIMUM_TRAINING_TYPES)
                    .build();
        }

        return Health.down()
                .withDetail("trainingTypeCount", count)
                .withDetail("minimumRequired", MINIMUM_TRAINING_TYPES)
                .withDetail("reason", "Training type catalog is incomplete")
                .build();
    }
}
