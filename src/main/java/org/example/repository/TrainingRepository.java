package org.example.repository;

import org.example.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, String>, TrainingRepositoryCustom {
}
