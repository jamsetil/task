package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.validation.NotBlankIfPresent;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TrainingCriteria {
    LocalDate fromDate;
    LocalDate toDate;
    @NotBlankIfPresent
    String trainerName;
    @NotBlankIfPresent
    String traineeName;
    @NotBlankIfPresent
    String trainingType;
}
