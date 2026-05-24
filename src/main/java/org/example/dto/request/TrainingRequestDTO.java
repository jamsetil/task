package org.example.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingRequestDTO {
    @NotBlank
    String traineeUsername;
    @NotBlank
    String trainerUsername;
    @NotBlank
    String trainingName;
    @NotBlank
    String trainingTypeName;
    @NotNull
    LocalDate trainingDate;
    @NotNull
    @Positive
    Integer trainingDuration;
}
