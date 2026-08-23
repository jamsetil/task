package org.example.repository;

import org.example.model.TrainingType;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Profile("!docker")

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingTypeName(String trainingTypeName);
}
