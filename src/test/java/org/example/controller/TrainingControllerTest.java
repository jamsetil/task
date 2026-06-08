package org.example.controller;

import org.example.dto.request.TrainingRequestDTO;
import org.example.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private TrainingController trainingController;

    @Test
    void createTraining_returnsOk() {
        var request = TrainingRequestDTO.builder()
                .traineeUsername("john.smith")
                .trainerUsername("ilyas.azizzade")
                .trainingName("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        var response = trainingController.createTraining(request, "pwd");

        verify(trainingService).createTraining(request, "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
