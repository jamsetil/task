package org.example.dto.request.create;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.dto.request.base.BaseCreateRequestDTO;

@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class TrainerCreateRequestDTO extends BaseCreateRequestDTO {
    @NotBlank
    String specializationName;
}
