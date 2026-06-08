package org.example.dto.request.base;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.model.Training;
import org.example.validation.NotBlankIfPresent;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BaseUpdateRequestDTO {
    @NotBlank
    String firstName;
    @NotBlank
    String lastName;
    @NotNull
    Boolean isActive;
}
