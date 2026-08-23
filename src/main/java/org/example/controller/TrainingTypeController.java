package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.service.TrainingTypeService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Tag(name = "training types", description = "training type catalog")
@Profile("!docker")

public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @Operation(summary = "get training types", description = "returns all training types")
    public ResponseEntity<List<TrainingTypeResponseDTO>> getTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getTrainingTypes());
    }
}
