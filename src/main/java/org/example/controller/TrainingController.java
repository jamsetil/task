package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.request.TrainingRequestDTO;
import org.example.service.TrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainings")
@RequiredArgsConstructor
@Api(value = "trainings", tags = "trainings")
public class TrainingController {
    private final TrainingService trainingService;

    @PostMapping
    @ApiOperation(value = "add training", notes = "create training session")
    public ResponseEntity<Void> createTraining(
            @RequestBody @Valid TrainingRequestDTO requestDTO,
            @ApiParam(value = "password", required = true) @RequestParam(name = "password") String password) {
        trainingService.createTraining(requestDTO, password);
        return ResponseEntity.ok().build();
    }
}
