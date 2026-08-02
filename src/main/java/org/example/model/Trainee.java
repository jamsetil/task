package org.example.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.model.base.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@NamedQueries({
        @NamedQuery(
        name = "findTraineeByUsernameAndPassword",
        query = "SELECT t FROM Trainee t WHERE t.user.userName = :username AND t.user.password = :password"),
        @NamedQuery(
                name = "Trainee.findByUsername",
                query = "SELECT t FROM Trainee t WHERE t.user.userName = :username")})
public class Trainee {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    String traineeId;
    LocalDate dateOfBirth;
    String address;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    User user;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "trainee", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    List<Training> trainings = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "trainee_trainers",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id")
    )
    List<Trainer> trainers;
}
