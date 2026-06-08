package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainingRequestDTO;
import org.example.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@Tag(name = "trainings", description = "training session operations")
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "add training", description = "create training session")
    public ResponseEntity<Void> createTraining(
            @RequestBody @Valid TrainingRequestDTO requestDTO,
            @Parameter(description = "password", required = true) @RequestParam(name = "password") String password) {
        trainingService.createTraining(requestDTO, password);
        return ResponseEntity.ok().build();
    }
}
