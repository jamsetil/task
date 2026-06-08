package org.example.controller;

import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @Test
    void getTrainingTypes_returnsList() {
        when(trainingTypeService.getTrainingTypes()).thenReturn(List.of(
                new TrainingTypeResponseDTO("1", "Yoga"),
                new TrainingTypeResponseDTO("2", "Cardio")
        ));

        var response = trainingTypeController.getTrainingTypes();

        assertEquals(2, response.getBody().size());
        assertEquals("Yoga", response.getBody().get(0).getTrainingType());
    }
}
