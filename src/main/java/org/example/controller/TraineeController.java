package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "trainees", tags = "trainees")
public class TraineeController {
    private final TraineeService traineeService;

    @PostMapping
    @ApiOperation(value = "register trainee", notes = "create trainee profile")
    public ResponseEntity<TraineeResponseDTO> createTrainee(
            @RequestBody @Valid TraineeCreateRequestDTO requestDTO) {
        return ResponseEntity.ok(traineeService.createTrainee(requestDTO));
    }

    @GetMapping("/{username}")
    @ApiOperation(value = "get trainee profile", notes = "returns trainee by username")
    public ResponseEntity<TraineeResponseDTO> getTraineeByUserName(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(traineeService.getTrainee(username, password));
    }

    @PutMapping("/{username}")
    @ApiOperation(value = "update trainee profile", notes = "updates trainee fields")
    public ResponseEntity<TraineeResponseDTO> updateTrainee(
            @RequestBody @Valid TraineeRequestDTO requestDTO,
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(traineeService.updateTrainee(requestDTO, username, password));
    }

    @PatchMapping("/{username}/status")
    @ApiOperation(value = "update trainee status", notes = "activate or deactivate trainee")
    public ResponseEntity<Void> toggleTraineeStatus(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "is active", required = true) @RequestParam(name = "isActive") boolean isActive,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        traineeService.changeStatus(username, isActive, password);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{username}")
    @ApiOperation(value = "delete trainee profile", notes = "hard delete trainee and trainings")
    public ResponseEntity<Void> deleteTrainee(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        traineeService.deleteTrainee(username, password);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{username}/trainers")
    @ApiOperation(value = "update trainer list", notes = "assign trainers to trainee")
    public ResponseEntity<TraineeResponseDTO> updateTraineeTrainers(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @RequestBody @Valid TraineeTrainersUpdateRequestDTO requestDTO,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(traineeService.updateTraineeTrainers(username, requestDTO.getTrainerUsernames(), password));
    }

    @GetMapping("/{username}/available-trainers")
    @ApiOperation(value = "get available trainers", notes = "active trainers not assigned to trainee")
    public ResponseEntity<List<TrainerResponseDTO>> getUnassignedTrainers(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        return ResponseEntity.ok(traineeService.getUnassignedTrainers(username, password));
    }

    @GetMapping("/{username}/trainings")
    @ApiOperation(value = "get trainee trainings", notes = "list trainings with optional filters")
    public ResponseEntity<TraineeResponseDTO> getTraineeTrainings(
            @ApiParam(value = "username", required = true) @PathVariable(name = "username") String username,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password,
            @ApiParam(value = "from date") @RequestParam(name = "fromDate", required = false) LocalDate fromDate,
            @ApiParam(value = "to date") @RequestParam(name = "toDate", required = false) LocalDate toDate,
            @ApiParam(value = "trainer name") @RequestParam(name = "trainerName", required = false) String trainerName,
            @ApiParam(value = "training type") @RequestParam(name = "trainingType", required = false) String trainingType) {
        return ResponseEntity.ok(traineeService.getTraineeTrainings(username, password,
                fromDate, toDate, trainerName, trainingType));
    }
}
