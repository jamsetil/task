package org.example.dao;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.example.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingTypeDAO {

    private final EntityManagerFactory emf;

    public Optional<TrainingType> findByName(String name) {
        try (var em = emf.createEntityManager()) {
            var results = em.createQuery(
                            "SELECT tt FROM TrainingType tt WHERE tt.trainingTypeName = :name",
                            TrainingType.class)
                    .setParameter("name", name)
                    .getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        }
    }
}
