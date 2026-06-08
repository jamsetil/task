package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.service.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Api(value = "training types", tags = "training types")
public class TrainingTypeController {
    private final TrainingTypeService trainingTypeService;

    @GetMapping
    @ApiOperation(value = "get training types", notes = "returns all training types")
    public ResponseEntity<List<TrainingTypeResponseDTO>> getTrainingTypes() {
        return ResponseEntity.ok(trainingTypeService.getTrainingTypes());
    }
}
