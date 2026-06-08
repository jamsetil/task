package org.example.controller;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @Test
    void createTrainer_returnsCreatedProfile() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .specializationName("Fitness")
                .build();
        var dto = TrainerResponseDTO.builder()
                .userName("ilyas.azizzade")
                .password("secret1234")
                .build();
        when(trainerService.createTrainer(request)).thenReturn(dto);

        var response = trainerController.createTrainer(request);

        assertEquals("ilyas.azizzade", response.getBody().getUserName());
    }

    @Test
    void getTrainerByUserName_returnsProfile() {
        var dto = TrainerResponseDTO.builder().userName("ilyas.azizzade").firstName("Ilyas").build();
        when(trainerService.getTrainer("ilyas.azizzade", "pwd")).thenReturn(dto);

        var response = trainerController.getTrainerByUserName("ilyas.azizzade", "pwd");

        assertEquals("Ilyas", response.getBody().getFirstName());
    }

    @Test
    void updateTrainer_returnsUpdatedProfile() {
        var request = TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .build();
        var dto = TrainerResponseDTO.builder().userName("ilyas.azizzade").build();
        when(trainerService.updateTrainer(request, "ilyas.azizzade", "pwd")).thenReturn(dto);

        var response = trainerController.updateTrainer(request, "ilyas.azizzade", "pwd");

        assertEquals("ilyas.azizzade", response.getBody().getUserName());
    }

    @Test
    void toggleTrainerStatus_returnsOk() {
        var response = trainerController.toggleTrainerStatus("ilyas.azizzade", false, "pwd");

        verify(trainerService).toggleTrainerStatus("ilyas.azizzade", false, "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getTrainerTrainings_returnsTrainings() {
        var dto = TrainerResponseDTO.builder().userName("ilyas.azizzade").build();
        when(trainerService.getTrainerTrainings("ilyas.azizzade", "pwd",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "John")).thenReturn(dto);

        var response = trainerController.getTrainerTrainings("ilyas.azizzade", "pwd",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31), "John");

        assertEquals("ilyas.azizzade", response.getBody().getUserName());
    }
}
