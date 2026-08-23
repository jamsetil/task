package org.example.repository;

import org.example.dto.TrainingCriteria;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.base.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!docker")

public class TrainingRepositoryImpl implements TrainingRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Training> findByTraineeUsername(String traineeUsername, TrainingCriteria criteria) {
        return findTrainings(traineeUsername, null, criteria);
    }

    @Override
    public List<Training> findByTrainerUsername(String trainerUsername, TrainingCriteria criteria) {
        return findTrainings(null, trainerUsername, criteria);
    }

    private List<Training> findTrainings(String traineeUsername, String trainerUsername, TrainingCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> training = cq.from(Training.class);

        Join<Training, Trainee> trainee = training.join("trainee");
        Join<Trainee, User> traineeUser = trainee.join("user");
        Join<Training, Trainer> trainer = training.join("trainer");
        Join<Trainer, User> trainerUser = trainer.join("user");

        List<Predicate> predicates = new ArrayList<>();

        if (traineeUsername != null) {
            predicates.add(cb.equal(traineeUser.get("userName"), traineeUsername));
        }

        if (trainerUsername != null) {
            predicates.add(cb.equal(trainerUser.get("userName"), trainerUsername));
        }

        if (criteria != null) {
            if (criteria.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), criteria.getFromDate()));
            }
            if (criteria.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), criteria.getToDate()));
            }
            if (criteria.getTrainerName() != null && !criteria.getTrainerName().isBlank()) {
                String trainerName = "%" + criteria.getTrainerName().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(trainerUser.get("firstName")), trainerName),
                        cb.like(cb.lower(trainerUser.get("lastName")), trainerName)
                ));
            }
            if (criteria.getTraineeName() != null && !criteria.getTraineeName().isBlank()) {
                String traineeName = "%" + criteria.getTraineeName().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(traineeUser.get("firstName")), traineeName),
                        cb.like(cb.lower(traineeUser.get("lastName")), traineeName)
                ));
            }
            if (criteria.getTrainingType() != null && !criteria.getTrainingType().isBlank()) {
                predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"), criteria.getTrainingType()));
            }
        }

        cq.select(training)
                .where(predicates.toArray(new Predicate[0]))
                .orderBy(cb.desc(training.get("trainingDate")));

        return entityManager.createQuery(cq).getResultList();
    }
}
