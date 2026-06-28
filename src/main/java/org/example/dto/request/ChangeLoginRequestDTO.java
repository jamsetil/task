package org.example.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLoginRequestDTO {
    @NotBlank
    @ToString.Exclude
    private String oldPassword;
    @NotBlank
    @ToString.Exclude
    private String newPassword;
}
