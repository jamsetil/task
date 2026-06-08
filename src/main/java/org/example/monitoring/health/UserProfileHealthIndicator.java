package org.example.monitoring.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component("userProfile")
@RequiredArgsConstructor
public class UserProfileHealthIndicator implements HealthIndicator {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public Health health() {
        try {
            long traineeCount = traineeRepository.count();
            long trainerCount = trainerRepository.count();
            log.debug("User profile health check, trainees={}, trainers={}", traineeCount, trainerCount);

            return Health.up()
                    .withDetail("traineeCount", traineeCount)
                    .withDetail("trainerCount", trainerCount)
                    .build();
        } catch (Exception ex) {
            log.warn("User profile health check failed", ex);
            return Health.down(ex)
                    .withDetail("reason", "Unable to read profile counts from database")
                    .build();
        }
    }
}
