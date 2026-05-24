package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.example.model.Trainer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainerDAO {

    private final EntityManagerFactory emf;

    public Trainer save(Trainer trainer) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            em.persist(trainer);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
        }
        return trainer;
    }

    public Optional<Trainer> find(String username) {
        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.createNamedQuery("Trainer.findByUsername", Trainer.class)
                    .setParameter("username", username)
                    .getSingleResult());
        }
    }

    public boolean matchTrainer(String username, String password) {
        try (var em = emf.createEntityManager()) {
            var result = em.createNamedQuery("findTrainerByUsernameAndPassword", Trainer.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getResultList();
            return !result.isEmpty();
        }
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        var em = emf.createEntityManager();
        try {
            var trainer = em.createNamedQuery("findTrainerByUsernameAndPassword", Trainer.class)
                    .setParameter("username", username)
                    .setParameter("password", oldPassword)
                    .getSingleResult();

            if (trainer != null) {
                em.getTransaction().begin();
                trainer.getUser().setPassword(newPassword);
                em.merge(trainer);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            em.getTransaction().rollback();
            return false;
        } finally {
            em.close();
        }
    }

    public Trainer toggleStatus(String username) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Trainer entity = em.createNamedQuery("Trainer.findByUsername", Trainer.class)
                    .setParameter("username", username)
                    .getSingleResult();

            entity.getUser().setIsActive(!entity.getUser().getIsActive());
            em.merge(entity);

            tx.commit();

            return entity;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public List<Trainer> findTrainersNotAssignedToTrainee(String traineeUsername) {
        EntityManager em = emf.createEntityManager();

        try {
            return em.createQuery("""
                SELECT tr
                FROM Trainer tr
                WHERE tr NOT IN (
                    SELECT assigned
                    FROM Trainee t
                    JOIN t.trainers assigned
                    WHERE t.user.userName = :username
                )
                """, Trainer.class)
                    .setParameter("username", traineeUsername)
                    .getResultList();

        } finally {
            em.close();
        }
    }


    public Trainer update(String username, Trainer trainer) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Trainer entity = em.createNamedQuery("Trainer.findByUsername", Trainer.class)
                    .setParameter("username", username)
                    .getSingleResult();


            entity.getUser().setFirstName(trainer.getUser().getFirstName());
            entity.getUser().setLastName(trainer.getUser().getLastName());
            entity.getUser().setUserName(trainer.getUser().getUserName());
            entity.getUser().setIsActive(trainer.getUser().getIsActive());

            tx.commit();

            return entity;

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
