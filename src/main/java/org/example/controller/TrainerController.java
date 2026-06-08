package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.service.TrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/trainers")
@RequiredArgsConstructor
@Tag(name = "trainers", description = "trainer profile operations")
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @Operation(summary = "register trainer", description = "create trainer profile")
    public ResponseEntity<TrainerResponseDTO> createTrainer(
            @RequestBody @Valid TrainerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(trainerService.createTrainer(requestDTO));
    }

    @GetMapping("/{username}")
    @Operation(summary = "get trainer profile", description = "returns trainer by username")
    public ResponseEntity<TrainerResponseDTO> getTrainerByUserName(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(trainerService.getTrainer(username, password));
    }

    @PutMapping("/{username}")
    @Operation(summary = "update trainer profile", description = "updates trainer fields")
    public ResponseEntity<TrainerResponseDTO> updateTrainer(
            @RequestBody @Valid TrainerRequestDTO requestDTO,
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(trainerService.updateTrainer(requestDTO, username, password));
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "update trainer status", description = "activate or deactivate trainer")
    public ResponseEntity<Void> toggleTrainerStatus(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "is active", required = true) @RequestParam(name = "isActive") boolean isActive,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password) {
        trainerService.toggleTrainerStatus(username, isActive, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "get trainer trainings", description = "list trainings with optional filters")
    public ResponseEntity<TrainerResponseDTO> getTrainerTrainings(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password,
            @Parameter(description = "from date") @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @Parameter(description = "to date") @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @Parameter(description = "trainee name") @RequestParam(name = "traineeName", required = false) String traineeName) {
        return ResponseEntity.ok(trainerService.getTrainerTrainings(username, password,
                fromDate, toDate, traineeName));
    }
}
