package org.example.service.impl;

import org.example.dao.TrainingDAO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Training;
import org.example.data.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDAO trainingDAO;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void shouldCreateTrainingSuccessfully() {

        TrainingRequestDTO request = MockData.getTrainingRequestDTO();

        when(trainingDAO.save(any(Training.class))).thenReturn(MockData.getTraining());

        Training result = trainingService.createTraining(request);



        assertNotNull(result);

        assertEquals("Cardio", result.getTrainingName());
        assertEquals(60, result.getTrainingDuration());

        assertEquals(
                LocalDate.of(2025, 1, 1),
                result.getTrainingDate()
        );

        assertEquals("trainee-1", result.getTraineeId());
        assertEquals("trainer-1", result.getTrainerId());

        assertNotNull(result.getTrainingId());

        verify(trainingDAO, times(1))
                .save(any(Training.class));
    }

    @Test
    void shouldReturnTrainingSuccessfully() {
        String trainingId = "1";

        Training training = MockData.getTraining();

        when(trainingDAO.find(trainingId))
                .thenReturn(Optional.of(training));

        Training result = trainingService.getTraining(trainingId);

        assertNotNull(result);

        assertEquals("1", result.getTrainingId());
        assertEquals("Cardio", result.getTrainingName());
        assertEquals(60, result.getTrainingDuration());

        verify(trainingDAO).find(trainingId);
    }

    @Test
    void shouldThrowExceptionWhenTrainingNotFound() {
        String trainingId = "1";

        when(trainingDAO.find(trainingId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> trainingService.getTraining(trainingId)
        );

        assertEquals(
                "Training not found with id: 1",
                exception.getMessage()
        );

        verify(trainingDAO).find(trainingId);
    }
}