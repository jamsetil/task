package org.example.dao;

import org.example.dao.support.DaoTestSupport;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeDAOTest extends DaoTestSupport {

    private TrainingTypeDAO trainingTypeDAO;

    @BeforeEach
    void setUp() {
        setUpEntityManager();
        trainingTypeDAO = new TrainingTypeDAO(emf);
    }

    @Test
    void findByName_returnsTypeWhenFound() {
        var type = TrainingType.builder().trainingTypeName("Fitness").build();
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList()).thenReturn(List.of(type));

        Optional<TrainingType> result = trainingTypeDAO.findByName("Fitness");

        assertTrue(result.isPresent());
        assertEquals("Fitness", result.get().getTrainingTypeName());
        verify(em).close();
    }

    @Test
    void findByName_returnsEmptyWhenNotFound() {
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList()).thenReturn(List.of());

        assertTrue(trainingTypeDAO.findByName("Unknown").isEmpty());
    }
}
