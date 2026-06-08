package org.example.dto.request.create;

import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseCreateRequestDTO;
import org.example.validation.NotBlankIfPresent;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = false)
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCreateRequestDTO extends BaseCreateRequestDTO {
    LocalDate dateOfBirth;
    String address;
}
