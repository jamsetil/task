package org.example.dto.request.base;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BaseCreateRequestDTO {
    @NotBlank
    String firstName;
    @NotBlank
    String lastName;
    @NotBlank
    String userName;
    @NotBlank
    String password;
    @NotBlank
    Boolean isActive;
}
