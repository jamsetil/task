package org.example.model;

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
public class Trainer extends User {
    String userId;
    String specialization;
    //1:N
    List<Training> trainings;
    List<TrainingType> trainingTypes;
}
