package org.example.dao;

import org.example.model.Trainee;
import org.example.data.MockData;
import org.junit.jupiter.api.BeforeEach;
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
class TraineeDAOTest {

    @Mock
    private Map<String, Trainee> traineeTable;

    @InjectMocks
    private TraineeDAO traineeDAO;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = MockData.getTrainee();
        trainee.setUserId("user123");
    }

    @Test
    void save_ShouldStoreTraineeInMap() {
        traineeDAO.save(trainee);

        verify(traineeTable).put("user123", trainee);
    }

    @Test
    void find_ShouldReturnTrainee_WhenExists() {
        when(traineeTable.get("user123")).thenReturn(trainee);
        Optional<Trainee> result = traineeDAO.find("user123");

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());

        verify(traineeTable).get("user123");
    }

    @Test
    void find_ShouldReturnEmpty_WhenNotFound() {
        when(traineeTable.get("unknown")).thenReturn(null);

        Optional<Trainee> result = traineeDAO.find("unknown");

        assertFalse(result.isPresent());
        verify(traineeTable).get("unknown");
    }

    @Test
    void delete_ShouldRemoveTraineeFromMap() {
        traineeDAO.delete("user123");

        verify(traineeTable).remove("user123");
    }
}