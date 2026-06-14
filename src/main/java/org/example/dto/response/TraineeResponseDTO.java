package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeResponseDTO {
    String firstName;
    String lastName;
    Boolean isActive;
    String userId;
    String userName;
    @ToString.Exclude
    String password;
    @ToString.Exclude
    String token;
    String dateOfBirth;
    String address;
    String trainerName;
    List<TrainerResponseDTO> trainerList;
    List<TrainingResponseDTO> trainings;
}
