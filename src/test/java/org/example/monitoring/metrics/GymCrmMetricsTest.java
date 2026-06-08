package org.example.monitoring.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.example.repository.TraineeRepository;
import org.example.repository.TrainerRepository;
import org.example.repository.TrainingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymCrmMetricsTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingRepository trainingRepository;

    private SimpleMeterRegistry meterRegistry;
    private GymCrmMetrics gymCrmMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        gymCrmMetrics = new GymCrmMetrics(meterRegistry, traineeRepository, trainerRepository, trainingRepository);
    }

    @Test
    void registersGaugesForStoredEntities() {
        when(traineeRepository.count()).thenReturn(3L);
        when(trainerRepository.count()).thenReturn(2L);
        when(trainingRepository.count()).thenReturn(7L);

        gymCrmMetrics.registerMetrics();

        assertEquals(3.0, meterRegistry.get("gym.trainees.total").gauge().value());
        assertEquals(2.0, meterRegistry.get("gym.trainers.total").gauge().value());
        assertEquals(7.0, meterRegistry.get("gym.trainings.total").gauge().value());
    }

    @Test
    void incrementsCreationCounters() {
        gymCrmMetrics.registerMetrics();
        gymCrmMetrics.recordTraineeProfileCreated();
        gymCrmMetrics.recordTrainerProfileCreated();
        gymCrmMetrics.recordTrainingCreated();
        gymCrmMetrics.recordTrainingCreated();

        assertEquals(1.0, meterRegistry.get("gym.trainee.profiles.created").counter().count());
        assertEquals(1.0, meterRegistry.get("gym.trainer.profiles.created").counter().count());
        assertEquals(2.0, meterRegistry.get("gym.trainings.created").counter().count());
    }
}
