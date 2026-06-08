package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.model.TrainingType;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerResponseDTO {
    String trainerId;
    String userId;
    String firstName;
    String lastName;
    String userName;
    String password;
    @Builder.Default
    Boolean isActive = true;
    Long specializationId;
    List<TraineeResponseDTO> traineeResponseDTOList;
    List<TrainingResponseDTO> trainingResponseDTOList;
}
