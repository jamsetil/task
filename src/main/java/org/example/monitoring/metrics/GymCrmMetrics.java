package org.example.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymCrmMetrics {

    private final MeterRegistry meterRegistry;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingRepository trainingRepository;

    private Counter traineeProfilesCreated;
    private Counter trainerProfilesCreated;
    private Counter trainingsCreated;

    @PostConstruct
    void registerMetrics() {
        Gauge.builder("gym.trainees.total", traineeRepository, TraineeRepository::count)
                .description("Total trainee profiles stored in the database")
                .register(meterRegistry);

        Gauge.builder("gym.trainers.total", trainerRepository, TrainerRepository::count)
                .description("Total trainer profiles stored in the database")
                .register(meterRegistry);

        Gauge.builder("gym.trainings.total", trainingRepository, TrainingRepository::count)
                .description("Total training sessions stored in the database")
                .register(meterRegistry);

        traineeProfilesCreated = Counter.builder("gym.trainee.profiles.created")
                .description("Number of trainee profiles created")
                .register(meterRegistry);

        trainerProfilesCreated = Counter.builder("gym.trainer.profiles.created")
                .description("Number of trainer profiles created")
                .register(meterRegistry);

        trainingsCreated = Counter.builder("gym.trainings.created")
                .description("Number of training sessions created")
                .register(meterRegistry);

        log.info("Registered gym CRM Prometheus metrics");
    }

    public void recordTraineeProfileCreated() {
        traineeProfilesCreated.increment();
        log.debug("Incremented gym.trainee.profiles.created metric");
    }

    public void recordTrainerProfileCreated() {
        trainerProfilesCreated.increment();
        log.debug("Incremented gym.trainer.profiles.created metric");
    }

    public void recordTrainingCreated() {
        trainingsCreated.increment();
        log.debug("Incremented gym.trainings.created metric");
    }
}
