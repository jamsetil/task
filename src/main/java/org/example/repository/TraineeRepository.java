package org.example.repository;

import org.example.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, String> {

    @Query("SELECT t FROM Trainee t WHERE t.user.userName = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    @Query("""
            SELECT t FROM Trainee t
            LEFT JOIN FETCH t.trainings
            LEFT JOIN FETCH t.user
            WHERE t.user.userName = :username
            """)
    Optional<Trainee> findByUsernameForDelete(@Param("username") String username);
}
