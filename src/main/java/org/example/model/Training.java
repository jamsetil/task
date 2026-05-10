package org.example.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Training {
    String trainingId; //i added additional this field. there was no such field in task
    String traineeId;
    String trainerId;
    String trainingName;
    //1:N
    List<TrainingType> trainingType;
    LocalDate trainingDate;
    Integer trainingDuration;

}
