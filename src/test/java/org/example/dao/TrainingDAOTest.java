package org.example.dao;

import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.example.dao.support.DaoTestSupport;
import org.example.dto.TrainingCriteria;
import org.example.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDAOTest extends DaoTestSupport {

    private TrainingDAO trainingDAO;

    @BeforeEach
    void setUp() {
        setUpEntityManager();
        trainingDAO = new TrainingDAO(emf);
    }

    @Test
    void save_persistsNewTraining() {
        var training = Training.builder().trainingName("Cardio").build();

        Training result = trainingDAO.save(training);

        assertSame(training, result);
        verify(em).persist(training);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void save_mergesExistingTraining() {
        var training = Training.builder().trainingId("id-1").trainingName("Cardio").build();
        when(em.merge(training)).thenReturn(training);

        Training result = trainingDAO.save(training);

        assertEquals("id-1", result.getTrainingId());
        verify(em).merge(training);
        verify(em, never()).persist(training);
        verify(tx).commit();
    }

    @Test
    void save_onError_rollsBackAndThrows() {
        var training = Training.builder().build();
        doThrow(new RuntimeException("db error")).when(em).persist(training);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> trainingDAO.save(training));

        verify(tx).rollback();
    }

    @Test
    void find_returnsTrainingWhenPresent() {
        var training = Training.builder().trainingId("id-1").build();
        when(em.find(Training.class, "id-1")).thenReturn(training);

        Optional<Training> result = trainingDAO.find("id-1");

        assertTrue(result.isPresent());
        assertSame(training, result.get());
    }

    @Test
    void find_returnsEmptyWhenNotFound() {
        when(em.find(Training.class, "missing")).thenReturn(null);

        assertTrue(trainingDAO.find("missing").isEmpty());
    }

    @Test
    void findTrainingsByTraineeUsername_returnsResults() {
        var training = Training.builder().trainingId("id-1").build();
        mockCriteriaQuery(List.of(training));

        List<Training> result = trainingDAO.findTrainingsByTraineeUsername("john", null);

        assertEquals(1, result.size());
        verify(em).close();
    }

    @Test
    void findTrainingsByTrainerUsername_returnsResults() {
        var training = Training.builder().trainingId("id-1").build();
        mockCriteriaQuery(List.of(training));

        List<Training> result = trainingDAO.findTrainingsByTrainerUsername("trainer", null);

        assertEquals(1, result.size());
    }

    @Test
    void findTrainingsByTraineeUsername_appliesCriteriaFilters() {
        var training = Training.builder().trainingId("id-1").build();
        mockCriteriaQuery(List.of(training));

        var criteria = TrainingCriteria.builder()
                .fromDate(LocalDate.of(2024, 1, 1))
                .toDate(LocalDate.of(2024, 12, 31))
                .trainerName("Jane")
                .traineeName("John")
                .trainingType("Fitness")
                .build();

        List<Training> result = trainingDAO.findTrainingsByTraineeUsername("john", criteria);

        assertEquals(1, result.size());
    }

    @SuppressWarnings("unchecked")
    private void mockCriteriaQuery(List<Training> trainings) {
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<Training> cq = mock(CriteriaQuery.class);
        Root<Training> root = mock(Root.class);
        Join<Object, Object> join = mock(Join.class);
        Path<Object> path = mock(Path.class);
        Predicate predicate = mock(Predicate.class);
        Order order = mock(Order.class);
        TypedQuery<Training> typedQuery = mock(TypedQuery.class);

        when(em.getCriteriaBuilder()).thenReturn(cb);
        when(cb.createQuery(Training.class)).thenReturn(cq);
        when(cq.from(Training.class)).thenReturn(root);
        when(root.join(anyString())).thenReturn(join);
        when(join.join(anyString())).thenReturn(join);
        when(root.get(anyString())).thenReturn(path);
        when(join.get(anyString())).thenReturn(path);
        when(path.get(anyString())).thenReturn(path);
        when(cb.equal(any(), any())).thenReturn(predicate);
        when(cb.greaterThanOrEqualTo(any(Expression.class), any(LocalDate.class))).thenReturn(predicate);
        when(cb.lessThanOrEqualTo(any(Expression.class), any(LocalDate.class))).thenReturn(predicate);
        when(cb.like(any(Expression.class), anyString())).thenReturn(predicate);
        when(cb.lower(any(Expression.class))).thenReturn(mock(Expression.class));
        when(cb.or(any(Predicate.class), any(Predicate.class))).thenReturn(predicate);
        when(cq.select(root)).thenReturn(cq);
        when(cq.where(any(Predicate[].class))).thenReturn(cq);
        when(cb.desc(any(Expression.class))).thenReturn(order);
        when(cq.orderBy(order)).thenReturn(cq);
        when(em.createQuery(cq)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(trainings);
    }
}
