package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Api(value = "authentication", tags = "authentication")
public class AuthController {
    private final UserService userService;

    @GetMapping("/login")
    @ApiOperation(value = "login", notes = "authenticate with username and password")
    public ResponseEntity<Void> login(
            @ApiParam(value = "username", required = true) @RequestParam(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        userService.authenticate(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    @ApiOperation(value = "change password", notes = "update password using old and new values")
    public ResponseEntity<Void> changeLogin(@RequestBody @Valid ChangeLoginRequestDTO requestDTO) {
        userService.changePassword(requestDTO.getUsername(), requestDTO.getOldPassword(), requestDTO.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
