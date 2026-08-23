package org.example.repository;

import org.example.model.Training;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile("!docker")

public interface TrainingRepository extends JpaRepository<Training, String>, TrainingRepositoryCustom {
}
