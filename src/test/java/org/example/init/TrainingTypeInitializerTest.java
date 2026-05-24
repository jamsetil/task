package org.example.init;

import org.example.dao.support.DaoTestSupport;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeInitializerTest extends DaoTestSupport {

    private TrainingTypeInitializer initializer;

    @BeforeEach
    void setUp() {
        setUpEntityManager();
        initializer = new TrainingTypeInitializer(emf);
    }

    @Test
    void init_persistsAllDefaultTypesWhenMissing() {
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList()).thenReturn(List.of());

        initializer.init();

        verify(em, times(5)).persist(any(TrainingType.class));
        verify(tx).begin();
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void init_skipsTypesThatAlreadyExist() {
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList()).thenReturn(List.of(
                TrainingType.builder().trainingTypeName("Fitness").build()
        ));

        initializer.init();

        verify(em, never()).persist(any(TrainingType.class));
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void init_persistsOnlyMissingTypes() {
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList())
                .thenReturn(List.of())
                .thenReturn(List.of(TrainingType.builder().trainingTypeName("Fitness").build()))
                .thenReturn(List.of())
                .thenReturn(List.of())
                .thenReturn(List.of());

        initializer.init();

        verify(em, times(4)).persist(any(TrainingType.class));
        verify(tx).commit();
    }

    @Test
    void init_onPersistError_rollsBackAndDoesNotThrow() {
        var query = mockJpqlQuery(TrainingType.class);
        when(query.getResultList()).thenReturn(List.of());
        doThrow(new RuntimeException("persist failed")).when(em).persist(any(TrainingType.class));
        when(tx.isActive()).thenReturn(true);

        assertDoesNotThrow(() -> initializer.init());

        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void init_onTransactionBeginError_closesEntityManager() {
        doThrow(new RuntimeException("begin failed")).when(tx).begin();

        assertDoesNotThrow(() -> initializer.init());

        verify(em).close();
        verify(em, never()).persist(any());
    }
}
