package org.example.init;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.TrainingType;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingTypeInitializer {

    private static final List<String> DEFAULT_TYPES = List.of(
            "Fitness",
            "Yoga",
            "Body Building",
            "Cardio",
            "Strength"
    );

    private final EntityManagerFactory emf;

    @PostConstruct
    public void init() {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            for (String typeName : DEFAULT_TYPES) {
                var existing = em.createQuery(
                                "SELECT tt FROM TrainingType tt WHERE tt.trainingTypeName = :name",
                                TrainingType.class)
                        .setParameter("name", typeName)
                        .getResultList();
                if (existing.isEmpty()) {
                    em.persist(TrainingType.builder().trainingTypeName(typeName).build());
                    log.info("Seeded training type: {}", typeName);
                }
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            log.warn("Training type seed skipped: {}", e.getMessage());
        } finally {
            em.close();
        }
    }
}
