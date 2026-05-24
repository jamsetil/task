package org.example.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.*;
import org.example.dto.TrainingCriteria;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainingDAO {

    @Autowired
    private  EntityManagerFactory emf;


    public List<Training> findTrainingsByTraineeUsername(
            String traineeUsername,
            TrainingCriteria criteria
    ) {
        EntityManager em = emf.createEntityManager();

        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);

            Root<Training> training = cq.from(Training.class);

            Join<Training, Trainee> trainee = training.join("trainee");
            Join<Trainee, User> traineeUser = trainee.join("user");

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(
                    cb.equal(traineeUser.get("userName"), traineeUsername)
            );

            if (criteria != null) {
                if (criteria.getFromDate() != null) {
                    predicates.add(
                            cb.greaterThanOrEqualTo(
                                    training.get("trainingDate"),
                                    criteria.getFromDate()
                            )
                    );
                }

                if (criteria.getToDate() != null) {
                    predicates.add(
                            cb.lessThanOrEqualTo(
                                    training.get("trainingDate"),
                                    criteria.getToDate()
                            )
                    );
                }

                if (criteria.getTrainerName() != null && !criteria.getTrainerName().isBlank()) {
                    Join<Training, Trainer> trainer = training.join("trainer");
                    Join<Trainer, User> trainerUser = trainer.join("user");

                    String trainerName = "%" + criteria.getTrainerName().toLowerCase() + "%";

                    predicates.add(
                            cb.or(
                                    cb.like(cb.lower(trainerUser.get("firstName")), trainerName),
                                    cb.like(cb.lower(trainerUser.get("lastName")), trainerName)
                            )
                    );
                }

                if (criteria.getTrainingType() != null && !criteria.getTrainingType().isBlank()) {
                    predicates.add(
                            cb.equal(
                                    training.get("trainingType").get("trainingTypeName"),
                                    criteria.getTrainingType()
                            )
                    );
                }
            }

            cq.select(training)
                    .where(predicates.toArray(new Predicate[0]))
                    .orderBy(cb.desc(training.get("trainingDate")));

            return em.createQuery(cq).getResultList();

        } finally {
            em.close();
        }
    }
    public Training save(Training training) {
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            if (training.getTrainingId() == null) {
                em.persist(training);
            } else {
                training = em.merge(training);
            }

            em.getTransaction().commit();
            return training;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;

        } finally {
            em.close();
        }
    }

    public Optional<Training> find(String trainingId) {
        return null;
    }
}
