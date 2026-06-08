package org.example.init;

import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeInitializerTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingTypeInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new TrainingTypeInitializer(trainingTypeRepository);
    }

    @Test
    void init_persistsAllDefaultTypesWhenMissing() {
        when(trainingTypeRepository.findByTrainingTypeName(any())).thenReturn(Optional.empty());

        initializer.init();

        verify(trainingTypeRepository, times(5)).save(any(TrainingType.class));
    }

    @Test
    void init_skipsTypesThatAlreadyExist() {
        when(trainingTypeRepository.findByTrainingTypeName(any()))
                .thenReturn(Optional.of(TrainingType.builder().trainingTypeName("Fitness").build()));

        initializer.init();

        verify(trainingTypeRepository, never()).save(any(TrainingType.class));
    }

    @Test
    void init_persistsOnlyMissingTypes() {
        when(trainingTypeRepository.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.of(TrainingType.builder().trainingTypeName("Fitness").build()));
        when(trainingTypeRepository.findByTrainingTypeName("Yoga")).thenReturn(Optional.empty());
        when(trainingTypeRepository.findByTrainingTypeName("Body Building")).thenReturn(Optional.empty());
        when(trainingTypeRepository.findByTrainingTypeName("Cardio")).thenReturn(Optional.empty());
        when(trainingTypeRepository.findByTrainingTypeName("Strength")).thenReturn(Optional.empty());

        initializer.init();

        verify(trainingTypeRepository, times(4)).save(any(TrainingType.class));
    }
}
