package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.model.base.User;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@NamedQueries({
        @NamedQuery(
                name = "findTrainerByUsernameAndPassword",
                query = "SELECT t FROM Trainer t WHERE t.user.userName = :username AND t.user.password = :password"),
        @NamedQuery(
                name = "Trainer.findByUsername",
        query = "SELECT t FROM Trainer t WHERE t.user.userName = :username")})
public class Trainer {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    String trainerId;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "specialization_id", nullable = false)
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    User user;
    //1:N
    @OneToMany(mappedBy = "trainer")
    List<Training> trainings;

    @ManyToMany(mappedBy = "trainers")
    List<Trainee> trainees;

}
