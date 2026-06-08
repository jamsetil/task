package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingResponseDTO {
    String trainingName;
    String trainingDate;
    String trainingType;
    int trainingDuration;
    String trainerName;
    String traineeName;
}
