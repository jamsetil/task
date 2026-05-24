package org.example.dto.request.base;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.example.model.Training;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class BaseUpdateRequestDTO {
    String username;
    String firstName;
    String lastName;
    Boolean isActive;
    boolean wantsPasswordChange;
    List<Training> trainings;
}
