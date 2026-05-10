package org.example.dao;

import org.example.model.Trainer;
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
class TrainerDAOTest {

    @Mock
    private Map<String, Trainer> trainerTable;

    @InjectMocks
    private TrainerDAO trainerDAO;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = MockData.getTrainer();
        trainer.setUserId("trainer123");
    }

    @Test
    void save_ShouldStoreTrainerInMap() {
        when(trainerTable.put("trainer123", trainer)).thenReturn(null);

        Trainer result = trainerDAO.save(trainer);

        assertNull(result);
        verify(trainerTable).put("trainer123", trainer);
    }

    @Test
    void find_ShouldReturnTrainer_WhenExists() {
        when(trainerTable.get("trainer123")).thenReturn(trainer);

        Optional<Trainer> result = trainerDAO.find("trainer123");

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());

        verify(trainerTable).get("trainer123");
    }

    @Test
    void find_ShouldReturnEmpty_WhenTrainerNotFound() {
        when(trainerTable.get("unknown")).thenReturn(null);

        Optional<Trainer> result = trainerDAO.find("unknown");

        assertFalse(result.isPresent());

        verify(trainerTable).get("unknown");
    }

    @Test
    void update_ShouldReplaceExistingTrainer() {
        Trainer oldTrainer = new Trainer();
        oldTrainer.setUserId("trainer123");

        when(trainerTable.put("trainer123", trainer))
                .thenReturn(oldTrainer);

        Trainer result = trainerDAO.update("trainer123", trainer);

        assertEquals(oldTrainer, result);

        verify(trainerTable).put("trainer123", trainer);
    }
}