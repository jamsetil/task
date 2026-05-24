package org.example.dto.request.create;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseCreateRequestDTO;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
@SuperBuilder
public class TraineeCreateRequestDTO extends BaseCreateRequestDTO {
    LocalDate dateOfBirth;
    String address;
}
