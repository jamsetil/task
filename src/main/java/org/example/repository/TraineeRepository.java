package org.example.repository;

import org.example.model.Trainee;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@Profile("!docker")
public interface TraineeRepository extends JpaRepository<Trainee, String> {

    @Query("SELECT t FROM Trainee t WHERE t.user.userName = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    @Query("""
            SELECT DISTINCT t FROM Trainee t
            LEFT JOIN FETCH t.trainings tr
            LEFT JOIN FETCH tr.trainer trainer
            LEFT JOIN FETCH trainer.user
            LEFT JOIN FETCH t.user
            WHERE t.user.userName = :username
            """)
    Optional<Trainee> findByUsernameForDelete(@Param("username") String username);
}
