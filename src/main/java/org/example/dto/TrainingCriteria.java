package org.example.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TrainingCriteria {
    LocalDate fromDate;
    LocalDate toDate;
    String trainerName;
    String traineeName;
    String trainingType;
}
