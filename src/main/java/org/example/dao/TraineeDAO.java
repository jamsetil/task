package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TraineeDAO {

    private final EntityManagerFactory emf;

    public void save(Trainee trainee) {
        var em = emf.createEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(trainee);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }

    }

    public Optional<Trainee> find(String username) {

        var em = emf.createEntityManager();

        try {
            return Optional.ofNullable(em.createNamedQuery("Trainee.findByUsername", Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult());
        } finally {
            em.close();
        }
    }

    public boolean matchTrainee(String username, String password) {
        try (var em = emf.createEntityManager()) {
            var result = em.createNamedQuery("findTraineeByUsernameAndPassword", Trainee.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getResultList();
            return !result.isEmpty();
        }
    }

    public Trainee changePassword(String username, String oldPassword, String newPassword) {
        var em = emf.createEntityManager();
        try {
            var trainee = em.createNamedQuery("findTraineeByUsernameAndPassword", Trainee.class)
                    .setParameter("username", username)
                    .setParameter("password", oldPassword)
                    .getSingleResult();

            if (trainee != null) {
                em.getTransaction().begin();
                trainee.getUser().setPassword(newPassword);
                em.merge(trainee);
                em.getTransaction().commit();
                return trainee;
            }
            return null;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Trainee toggleStatus(String username){
        var em = emf.createEntityManager();
        try {
            var trainee = em.createNamedQuery("Trainee.findByUsername", Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (trainee != null) {
                em.getTransaction().begin();
                trainee.getUser().setIsActive(!trainee.getUser().getIsActive());
                em.merge(trainee);
                em.getTransaction().commit();
                return trainee;
            }
            return null;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Trainee update(String username, Trainee trainee) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Trainee existingTrainee = em.createNamedQuery(
                            "Trainee.findByUsername",
                            Trainee.class
                    )
                    .setParameter("username", username)
                    .getSingleResult();

            existingTrainee.setDateOfBirth(trainee.getDateOfBirth());
            existingTrainee.setAddress(trainee.getAddress());

            existingTrainee.getUser().setFirstName(trainee.getUser().getFirstName());
            existingTrainee.getUser().setLastName(trainee.getUser().getLastName());
            existingTrainee.getUser().setUserName(trainee.getUser().getUserName());
            existingTrainee.getUser().setIsActive(trainee.getUser().getIsActive());

            tx.commit();

            return existingTrainee;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public void updateTraineeTrainers(
            String traineeUsername,
            List<String> trainerUsernames
    ) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Trainee trainee = em.createQuery("""
                SELECT t
                FROM Trainee t
                WHERE t.user.userName = :username
                """, Trainee.class)
                    .setParameter("username", traineeUsername)
                    .getSingleResult();

            List<Trainer> trainers = em.createQuery("""
                SELECT t
                FROM Trainer t
                WHERE t.user.userName IN :usernames
                """, Trainer.class)
                    .setParameter("usernames", trainerUsernames)
                    .getResultList();

            trainee.setTrainers(trainers);

            em.merge(trainee);

            em.getTransaction().commit();

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void delete(String username) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Trainee trainee = em.createQuery("""
                            SELECT t
                            FROM Trainee t
                            LEFT JOIN FETCH t.trainings
                            LEFT JOIN FETCH t.user
                            WHERE t.user.userName = :username
                            """, Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult();

            em.remove(trainee);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }
}