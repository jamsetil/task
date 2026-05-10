package org.example.dao;

import org.example.data.MockData;
import org.example.model.Training;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDAOTest {

    @Mock
    private Map<String, Training> trainingTable;

    @InjectMocks
    private TrainingDAO trainingDAO;

    @Test
    void save_ShouldStoreTrainingInMap() {
        Training training = MockData.getTraining();

        when(trainingTable.put("1", training)).thenReturn(null);

        Training result = trainingDAO.save(training);

        assertNull(result);

        verify(trainingTable).put("1", training);
    }

    @Test
    void find_ShouldReturnTraining_WhenExists() {
        Training training = MockData.getTraining();

        when(trainingTable.get("1")).thenReturn(training);

        Optional<Training> result = trainingDAO.find("1");

        assertTrue(result.isPresent());
        assertEquals(training, result.get());

        verify(trainingTable).get("1");
    }

    @Test
    void find_ShouldReturnEmpty_WhenTrainingNotFound() {
        when(trainingTable.get("999")).thenReturn(null);

        Optional<Training> result = trainingDAO.find("999");

        assertTrue(result.isEmpty());

        verify(trainingTable).get("999");
    }
}