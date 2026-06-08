package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    public List<Trainee> findTraineeTrainings(String username, String fromDate,
                                              String toDate, String trainerName, String trainingType) {

        try (var em = emf.createEntityManager()) {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Trainee> cq = cb.createQuery(Trainee.class);
            var trainee = cq.from(Trainee.class);

            List<Predicate> predicates = new ArrayList<>();

            Join<Trainee, Training> trainingJoin = trainee.join("trainings");

            predicates.add(
                    cb.equal(
                            trainee.get("user")
                                    .get("userName"),
                            username
                    )
            );

            if (fromDate != null) {

                predicates.add(
                        cb.greaterThanOrEqualTo(
                                trainingJoin.get("trainingDate"),
                                fromDate
                        )
                );
            }

            if (toDate != null) {

                predicates.add(
                        cb.lessThanOrEqualTo(
                                trainingJoin.get("trainingDate"),
                                toDate
                        )
                );
            }

            if (trainingType != null) {

                predicates.add(
                        cb.equal(
                                trainingJoin.get("trainingType"),
                                trainingType
                        )
                );
            }

            if (trainerName != null) {

                predicates.add(
                        cb.equal(
                                trainingJoin.get("trainer")
                                        .get("user")
                                        .get("firstName"),
                                trainerName
                        )
                );
            }

            cq.select(trainee).where(predicates.toArray(new Predicate[0]));

            return em.createQuery(cq).getResultList();
        }
    }

    public Optional<Trainee> find(String username) {

        try (var em = emf.createEntityManager()) {
            return Optional.ofNullable(em.createNamedQuery("Trainee.findByUsername", Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult());
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

    public Trainee toggleStatus(String username, boolean isActive){
        var em = emf.createEntityManager();
        try {
            var trainee = em.createNamedQuery("Trainee.findByUsername", Trainee.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (trainee != null) {
                em.getTransaction().begin();
                trainee.getUser().setIsActive(isActive);
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

    public Trainee update(Trainee trainee) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Trainee updatedTrainee = em.merge(trainee);

            tx.commit();

            return updatedTrainee;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public Trainee updateTraineeTrainers(
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

            return trainee;

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

            trainee.getTrainers().clear();
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