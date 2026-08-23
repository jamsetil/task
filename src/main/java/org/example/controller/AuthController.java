package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.response.JwtResponseDTO;
import org.example.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "authentication", description = "login and password operations")
@Profile("!docker")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "login", description = "authenticate with username and password in request body, returns JWT bearer token")
    public ResponseEntity<JwtResponseDTO> login(@RequestBody @Valid LoginRequestDTO requestDTO) {
        return ResponseEntity.ok(new JwtResponseDTO(
                userService.authenticate(requestDTO.getUsername(), requestDTO.getPassword())));
    }

    @PutMapping("/change-password")
    @Operation(summary = "change password", description = "update password using old and new values")
    public ResponseEntity<Void> changeLogin(@RequestBody @Valid ChangeLoginRequestDTO requestDTO) {
        userService.changePassword(requestDTO.getOldPassword(), requestDTO.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
