package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainingRequestDTO;
import org.example.service.TrainingService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@Tag(name = "trainings", description = "training session operations")
@Profile("!docker")

public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @Operation(summary = "add training", description = "create training session")
    public ResponseEntity<Void> createTraining(@RequestBody @Valid TrainingRequestDTO requestDTO) {
        trainingService.createTraining(requestDTO);
        return ResponseEntity.ok().build();
    }
}
