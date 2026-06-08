package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "authentication", description = "login and password operations")
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    @Operation(summary = "login", description = "authenticate with username and password")
    public ResponseEntity<Void> login(
            @Parameter(description = "username", required = true) @RequestParam(name = "username") String username,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password) {
        userService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @Operation(summary = "change password", description = "update password using old and new values")
    public ResponseEntity<Void> changeLogin(@RequestBody @Valid ChangeLoginRequestDTO requestDTO) {
        userService.changePassword(requestDTO.getUsername(), requestDTO.getOldPassword(), requestDTO.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
