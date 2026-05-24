package org.example.dao;

import org.example.dao.support.DaoTestSupport;
import org.example.model.Trainer;
import org.example.model.base.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerDAOTest extends DaoTestSupport {

    private TrainerDAO trainerDAO;

    @BeforeEach
    void setUp() {
        setUpEntityManager();
        trainerDAO = new TrainerDAO(emf);
    }

    @Test
    void save_persistsAndReturnsTrainer() {
        var trainer = Trainer.builder().build();

        Trainer result = trainerDAO.save(trainer);

        assertSame(trainer, result);
        verify(em).persist(trainer);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void save_onError_rollsBackAndThrows() {
        var trainer = Trainer.builder().build();
        doThrow(new RuntimeException("db error")).when(em).persist(trainer);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> trainerDAO.save(trainer));

        verify(tx).rollback();
    }

    @Test
    void find_returnsTrainerWhenPresent() {
        var trainer = Trainer.builder().build();
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenReturn(trainer);

        Optional<Trainer> result = trainerDAO.find("trainer");

        assertTrue(result.isPresent());
        assertSame(trainer, result.get());
    }

    @Test
    void find_returnsEmptyWhenNotFound() {
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenReturn(null);

        assertTrue(trainerDAO.find("missing").isEmpty());
    }

    @Test
    void matchTrainer_returnsTrueWhenResultExists() {
        var query = mockNamedQuery(Trainer.class);
        when(query.getResultList()).thenReturn(List.of(Trainer.builder().build()));

        assertTrue(trainerDAO.matchTrainer("trainer", "pwd"));
    }

    @Test
    void matchTrainer_returnsFalseWhenNoResult() {
        var query = mockNamedQuery(Trainer.class);
        when(query.getResultList()).thenReturn(List.of());

        assertFalse(trainerDAO.matchTrainer("trainer", "pwd"));
    }

    @Test
    void changePassword_updatesPasswordAndReturnsTrue() {
        var user = User.builder().password("old").build();
        var trainer = Trainer.builder().user(user).build();
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenReturn(trainer);

        assertTrue(trainerDAO.changePassword("trainer", "old", "new"));

        assertEquals("new", user.getPassword());
        verify(em).merge(trainer);
        verify(tx).commit();
    }

    @Test
    void changePassword_onException_returnsFalse() {
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenThrow(new RuntimeException("not found"));

        assertFalse(trainerDAO.changePassword("trainer", "old", "new"));

        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void toggleStatus_invertsActiveFlag() {
        var user = User.builder().isActive(false).build();
        var trainer = Trainer.builder().user(user).build();
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenReturn(trainer);

        Trainer result = trainerDAO.toggleStatus("trainer");

        assertTrue(user.getIsActive());
        verify(em).merge(trainer);
        verify(tx).commit();
        assertSame(trainer, result);
    }

    @Test
    void toggleStatus_onError_rollsBackAndThrows() {
        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenThrow(new RuntimeException("not found"));
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> trainerDAO.toggleStatus("trainer"));

        verify(tx).rollback();
    }

    @Test
    void update_updatesExistingTrainerFields() {
        var existingUser = User.builder()
                .firstName("Old")
                .lastName("Name")
                .userName("old.trainer")
                .isActive(true)
                .build();
        var existing = Trainer.builder().user(existingUser).build();

        var updateUser = User.builder()
                .firstName("New")
                .lastName("Trainer")
                .userName("new.trainer")
                .isActive(false)
                .build();

        var query = mockNamedQuery(Trainer.class);
        when(query.getSingleResult()).thenReturn(existing);

        Trainer result = trainerDAO.update("trainer", Trainer.builder().user(updateUser).build());

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("new.trainer", result.getUser().getUserName());
        verify(tx).commit();
    }

    @Test
    void findTrainersNotAssignedToTrainee_returnsList() {
        var trainer = Trainer.builder().build();
        var query = mockJpqlQuery(Trainer.class);
        when(query.getResultList()).thenReturn(List.of(trainer));

        List<Trainer> result = trainerDAO.findTrainersNotAssignedToTrainee("john");

        assertEquals(1, result.size());
        verify(em).close();
    }
}
