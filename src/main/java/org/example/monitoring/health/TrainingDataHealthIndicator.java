package org.example.monitoring.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.TrainingRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("trainingData")
@RequiredArgsConstructor
public class TrainingDataHealthIndicator implements HealthIndicator {

    private final TrainingRepository trainingRepository;

    @Override
    public Health health() {
        try {
            long trainingCount = trainingRepository.count();
            log.debug("Training data health check, trainings={}", trainingCount);

            return Health.up()
                    .withDetail("trainingCount", trainingCount)
                    .build();
        } catch (Exception ex) {
            log.warn("Training data health check failed", ex);
            return Health.down(ex)
                    .withDetail("reason", "Unable to read training data from database")
                    .build();
        }
    }
}
