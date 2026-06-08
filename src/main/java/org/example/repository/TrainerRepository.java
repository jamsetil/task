package org.example.repository;

import org.example.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, String> {

    @Query("SELECT t FROM Trainer t WHERE t.user.userName = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);

    List<Trainer> findByUserUserNameIn(Collection<String> usernames);

    @Query("""
            SELECT tr FROM Trainer tr
            WHERE tr.user.isActive = true
            AND tr NOT IN (
                SELECT assigned FROM Trainee t JOIN t.trainers assigned WHERE t.user.userName = :username
            )
            """)
    List<Trainer> findActiveNotAssignedToTrainee(@Param("username") String username);
}
