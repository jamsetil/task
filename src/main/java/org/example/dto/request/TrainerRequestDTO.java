package org.example.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseCreateRequestDTO;
import org.example.model.TrainingType;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerRequestDTO extends BaseCreateRequestDTO {
    String specialization;
    List<TrainingType> trainingTypes;
}
