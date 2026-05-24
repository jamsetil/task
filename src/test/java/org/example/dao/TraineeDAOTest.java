package org.example.dao;

import org.example.dao.support.DaoTestSupport;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.base.User;
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
class TraineeDAOTest extends DaoTestSupport {

    private TraineeDAO traineeDAO;

    @BeforeEach
    void setUp() {
        setUpEntityManager();
        traineeDAO = new TraineeDAO(emf);
    }

    @Test
    void save_persistsAndCommits() {
        var trainee = Trainee.builder().build();

        traineeDAO.save(trainee);

        verify(em).persist(trainee);
        verify(tx).begin();
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void save_onError_rollsBackAndThrows() {
        var trainee = Trainee.builder().build();
        doThrow(new RuntimeException("db error")).when(em).persist(trainee);
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> traineeDAO.save(trainee));

        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void find_returnsTraineeWhenPresent() {
        var trainee = Trainee.builder().build();
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(trainee);

        Optional<Trainee> result = traineeDAO.find("john.smith");

        assertTrue(result.isPresent());
        assertSame(trainee, result.get());
        verify(em).close();
    }

    @Test
    void find_returnsEmptyWhenNotFound() {
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(null);

        assertTrue(traineeDAO.find("missing").isEmpty());
    }

    @Test
    void matchTrainee_returnsTrueWhenResultExists() {
        var query = mockNamedQuery(Trainee.class);
        when(query.getResultList()).thenReturn(List.of(Trainee.builder().build()));

        assertTrue(traineeDAO.matchTrainee("john", "pwd"));
    }

    @Test
    void matchTrainee_returnsFalseWhenNoResult() {
        var query = mockNamedQuery(Trainee.class);
        when(query.getResultList()).thenReturn(List.of());

        assertFalse(traineeDAO.matchTrainee("john", "pwd"));
    }

    @Test
    void changePassword_updatesPasswordAndReturnsTrainee() {
        var user = User.builder().password("old").build();
        var trainee = Trainee.builder().user(user).build();
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(trainee);

        Trainee result = traineeDAO.changePassword("john", "old", "new");

        assertSame(trainee, result);
        assertEquals("new", user.getPassword());
        verify(em).merge(trainee);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void changePassword_onError_rollsBackAndThrows() {
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenThrow(new RuntimeException("not found"));

        assertThrows(RuntimeException.class,
                () -> traineeDAO.changePassword("john", "old", "new"));

        verify(tx).rollback();
        verify(em).close();
    }

    @Test
    void toggleStatus_invertsActiveFlag() {
        var user = User.builder().isActive(true).build();
        var trainee = Trainee.builder().user(user).build();
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(trainee);

        Trainee result = traineeDAO.toggleStatus("john");

        assertSame(trainee, result);
        assertFalse(user.getIsActive());
        verify(em).merge(trainee);
        verify(tx).commit();
    }

    @Test
    void update_updatesExistingTraineeFields() {
        var existingUser = User.builder()
                .firstName("Old")
                .lastName("Name")
                .userName("old.user")
                .isActive(true)
                .build();
        var existing = Trainee.builder()
                .user(existingUser)
                .address("old address")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        var updateUser = User.builder()
                .firstName("New")
                .lastName("User")
                .userName("new.user")
                .isActive(false)
                .build();
        var update = Trainee.builder()
                .user(updateUser)
                .address("new address")
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .build();

        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(existing);

        Trainee result = traineeDAO.update("john", update);

        assertEquals("New", result.getUser().getFirstName());
        assertEquals("new address", result.getAddress());
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void update_onError_rollsBackAndThrows() {
        var query = mockNamedQuery(Trainee.class);
        when(query.getSingleResult()).thenThrow(new RuntimeException("not found"));
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> traineeDAO.update("john", Trainee.builder().user(User.builder().build()).build()));

        verify(tx).rollback();
    }

    @Test
    void updateTraineeTrainers_assignsTrainerList() {
        var trainee = Trainee.builder().build();
        var trainer = Trainer.builder().build();

        @SuppressWarnings("unchecked")
        var traineeQuery = (jakarta.persistence.TypedQuery<Trainee>) mock(jakarta.persistence.TypedQuery.class);
        @SuppressWarnings("unchecked")
        var trainerQuery = (jakarta.persistence.TypedQuery<Trainer>) mock(jakarta.persistence.TypedQuery.class);

        when(em.createQuery(anyString(), eq(Trainee.class))).thenReturn(traineeQuery);
        when(em.createQuery(anyString(), eq(Trainer.class))).thenReturn(trainerQuery);
        when(traineeQuery.setParameter(anyString(), any())).thenReturn(traineeQuery);
        when(trainerQuery.setParameter(anyString(), any())).thenReturn(trainerQuery);
        when(traineeQuery.getSingleResult()).thenReturn(trainee);
        when(trainerQuery.getResultList()).thenReturn(List.of(trainer));

        traineeDAO.updateTraineeTrainers("john", List.of("trainer1"));

        assertEquals(List.of(trainer), trainee.getTrainers());
        verify(em).merge(trainee);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_removesTrainee() {
        var trainee = Trainee.builder().build();
        var query = mockJpqlQuery(Trainee.class);
        when(query.getSingleResult()).thenReturn(trainee);

        traineeDAO.delete("john");

        verify(em).remove(trainee);
        verify(tx).commit();
        verify(em).close();
    }

    @Test
    void delete_onError_rollsBackAndThrows() {
        var query = mockJpqlQuery(Trainee.class);
        when(query.getSingleResult()).thenThrow(new RuntimeException("not found"));
        when(tx.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> traineeDAO.delete("john"));

        verify(tx).rollback();
    }
}
