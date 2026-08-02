package org.example.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Training {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    String trainingId;

    @Column(nullable = false)
    String trainingName;

    @Column(nullable = false)
    LocalDate trainingDate;
    @Column(nullable = false)
    Integer trainingDuration;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "trainee_id")
    Trainee trainee;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    Trainer trainer;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "training_type_id")
    TrainingType trainingType;
}
