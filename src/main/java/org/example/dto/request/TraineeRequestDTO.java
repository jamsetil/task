package org.example.dto.request;

import jakarta.validation.constraints.Past;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseUpdateRequestDTO;
import org.example.validation.NotBlankIfPresent;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TraineeRequestDTO extends BaseUpdateRequestDTO {

    LocalDate dateOfBirth;

    String address;
}
