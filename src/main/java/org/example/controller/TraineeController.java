package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.service.TraineeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
@Tag(name = "trainees", description = "trainee profile operations")
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @Operation(summary = "register trainee", description = "create trainee profile")
    public ResponseEntity<TraineeResponseDTO> createTrainee(
            @RequestBody @Valid TraineeCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(traineeService.createTrainee(requestDTO));
    }

    @GetMapping("/{username}")
    @Operation(summary = "get trainee profile", description = "returns trainee by username")
    public ResponseEntity<TraineeResponseDTO> getTraineeByUserName(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username) {
        return ResponseEntity.ok(traineeService.getTrainee(username));
    }

    @PutMapping("/{username}")
    @Operation(summary = "update trainee profile", description = "updates trainee fields")
    public ResponseEntity<TraineeResponseDTO> updateTrainee(
            @RequestBody @Valid TraineeRequestDTO requestDTO,
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username) {
        return ResponseEntity.ok(traineeService.updateTrainee(requestDTO, username));
    }

    @PatchMapping("/{username}/status")
    @Operation(summary = "update trainee status", description = "activate or deactivate trainee")
    public ResponseEntity<Void> toggleTraineeStatus(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "is active", required = true) @RequestParam(name = "isActive") boolean isActive) {
        traineeService.changeStatus(username, isActive);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "delete trainee profile", description = "hard delete trainee and trainings")
    public ResponseEntity<Void> deleteTrainee(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username) {
        traineeService.deleteTrainee(username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/trainers")
    @Operation(summary = "update trainer list", description = "assign trainers to trainee")
    public ResponseEntity<TraineeResponseDTO> updateTraineeTrainers(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @RequestBody @Valid TraineeTrainersUpdateRequestDTO requestDTO) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(username, requestDTO.getTrainerUsernames()));
    }

    @GetMapping("/{username}/available-trainers")
    @Operation(summary = "get available trainers", description = "active trainers not assigned to trainee")
    public ResponseEntity<List<TrainerResponseDTO>> getUnassignedTrainers(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username) {
        return ResponseEntity.ok(traineeService.getUnassignedTrainers(username));
    }

    @GetMapping("/{username}/trainings")
    @Operation(summary = "get trainee trainings", description = "list trainings with optional filters")
    public ResponseEntity<TraineeResponseDTO> getTraineeTrainings(
            @Parameter(description = "username", required = true) @PathVariable(name = "username") String username,
            @Parameter(description = "from date") @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @Parameter(description = "to date") @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @Parameter(description = "trainer name") @RequestParam(name = "trainerName", required = false) String trainerName,
            @Parameter(description = "training type") @RequestParam(name = "trainingType", required = false) String trainingType) {
        return ResponseEntity.ok(traineeService.getTraineeTrainings(username,
                fromDate, toDate, trainerName, trainingType));
    }
}
