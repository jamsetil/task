package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "trainers", tags = "trainers")
public class TrainerController {
    private final TrainerService trainerService;

    @PostMapping
    @ApiOperation(value = "register trainer", notes = "create trainer profile")
    public ResponseEntity<TrainerResponseDTO> createTrainer(
            @RequestBody @Valid TrainerCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(trainerService.createTrainer(requestDTO));
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "get trainer profile", notes = "returns trainer by username")
    public ResponseEntity<TrainerResponseDTO> getTrainerByUserName(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(trainerService.getTrainer(username, password));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "update trainer profile", notes = "updates trainer fields")
    public ResponseEntity<TrainerResponseDTO> updateTrainer(
            @RequestBody @Valid TrainerRequestDTO requestDTO,
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(trainerService.updateTrainer(requestDTO, username, password));
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "update trainer status", notes = "activate or deactivate trainer")
    public ResponseEntity<Void> toggleTrainerStatus(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "is active", required = true) @RequestParam(name = "isActive") boolean isActive,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        trainerService.toggleTrainerStatus(username, isActive, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation(value = "get trainer trainings", notes = "list trainings with optional filters")
    public ResponseEntity<TrainerResponseDTO> getTrainerTrainings(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password,
            @ApiParam(value = "from date") @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @ApiParam(value = "to date") @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @ApiParam(value = "trainee name") @RequestParam(name = "traineeName", required = false) String traineeName) {
        return ResponseEntity.ok(trainerService.getTrainerTrainings(username, password,
                fromDate, toDate, traineeName));
    }
}
