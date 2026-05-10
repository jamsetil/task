package org.example.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
