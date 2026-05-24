package org.example.dto.request.base;


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
    @NotBlankIfPresent
    String username;
    @NotBlankIfPresent
    String firstName;
    @NotBlankIfPresent
    String lastName;
    Boolean isActive;
    boolean wantsPasswordChange;
    List<Training> trainings;
}
