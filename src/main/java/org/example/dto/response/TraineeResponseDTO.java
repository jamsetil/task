package org.example.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.model.Training;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeResponseDTO {
    String firstName;
    String lastName;
    Boolean isActive;
    String userId;
    String userName;
    String dateOfBirth;
    String address;
    List<Training> trainings;
}
