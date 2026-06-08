package org.example.controller;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @Test
    void createTrainee_returnsCreatedProfile() {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .build();
        var responseDto = TraineeResponseDTO.builder()
                .userName("john.smith")
                .password("pwd1234567")
                .build();
        when(traineeService.createTrainee(request)).thenReturn(responseDto);

        var response = traineeController.createTrainee(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("john.smith", response.getBody().getUserName());
    }

    @Test
    void getTraineeByUserName_returnsProfile() {
        var dto = TraineeResponseDTO.builder().firstName("John").lastName("Smith").build();
        when(traineeService.getTrainee("john.smith", "pwd")).thenReturn(dto);

        var response = traineeController.getTraineeByUserName("john.smith", "pwd");

        assertEquals("John", response.getBody().getFirstName());
    }

    @Test
    void updateTrainee_returnsUpdatedProfile() {
        var request = TraineeRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true)
                .build();
        var dto = TraineeResponseDTO.builder().userName("john.smith").firstName("John").build();
        when(traineeService.updateTrainee(request, "john.smith", "pwd")).thenReturn(dto);

        var response = traineeController.updateTrainee(request, "john.smith", "pwd");

        assertEquals("john.smith", response.getBody().getUserName());
    }

    @Test
    void toggleTraineeStatus_returnsOk() {
        var response = traineeController.toggleTraineeStatus("john.smith", true, "pwd");

        verify(traineeService).changeStatus("john.smith", true, "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void deleteTrainee_returnsOk() {
        var response = traineeController.deleteTrainee("john.smith", "pwd");

        verify(traineeService).deleteTrainee("john.smith", "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void updateTraineeTrainers_returnsUpdatedList() {
        var request = TraineeTrainersUpdateRequestDTO.builder()
                .trainerUsernames(List.of("trainer1"))
                .build();
        var dto = TraineeResponseDTO.builder().userName("john.smith").build();
        when(traineeService.updateTraineeTrainers("john.smith", List.of("trainer1"), "pwd")).thenReturn(dto);

        var response = traineeController.updateTraineeTrainers("john.smith", request, "pwd");

        assertEquals("john.smith", response.getBody().getUserName());
    }

    @Test
    void getUnassignedTrainers_returnsList() {
        when(traineeService.getUnassignedTrainers("john.smith", "pwd"))
                .thenReturn(List.of(TrainerResponseDTO.builder().userName("trainer1").build()));

        var response = traineeController.getUnassignedTrainers("john.smith", "pwd");

        assertEquals(1, response.getBody().size());
    }

    @Test
    void getTraineeTrainings_returnsTrainings() {
        var dto = TraineeResponseDTO.builder().userName("john.smith").build();
        when(traineeService.getTraineeTrainings("john.smith", "pwd",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Ann", "Fitness")).thenReturn(dto);

        var response = traineeController.getTraineeTrainings("john.smith", "pwd",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "Ann", "Fitness");

        assertEquals("john.smith", response.getBody().getUserName());
    }
}
