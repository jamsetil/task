package org.example.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.example.model.Training;
import org.example.model.TrainingType;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerResponseDTO {
    String firstName;
    String lastName;
    String userName;
    Boolean isActive;
    String userId;
    String specialization;
    //1:N
    List<Training> trainings;
    List<TrainingType> trainingTypes;
}
