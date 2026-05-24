package org.example.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseUpdateRequestDTO;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeRequestDTO extends BaseUpdateRequestDTO{
    LocalDate dateOfBirth;
    String address;

}
