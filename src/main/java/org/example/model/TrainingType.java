package org.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trainingTypeId;
    @Column(nullable = false)
    private String trainingTypeName;
    @OneToMany
    private List<Training> training;
    @OneToMany
    private List<Trainer> trainer;

}
