package org.example.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.model.TrainingType;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingRequestDTO {
    String traineeId;
    String trainerId;
    String trainingName;
    TrainingType trainingType;
    LocalDate trainingDate;
    Integer trainingDuration;
}
