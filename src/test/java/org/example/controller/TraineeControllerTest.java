package org.example.controller;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TraineeTrainersUpdateRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.filter.JwtAuthFilter;
import org.example.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TraineeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraineeService traineeService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void createTrainee_returnsCreatedProfile() throws Exception {
        var request = TraineeCreateRequestDTO.builder()
                .firstName("Faiq")
                .lastName("Azizzade")
                .build();
        when(traineeService.createTrainee(any())).thenReturn(TraineeResponseDTO.builder()
                .userName("faiq.azizzade")
                .password("pwd1234567")
                .token("jwt-token")
                .build());

        mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("faiq.azizzade"));
    }

    @Test
    void getTraineeByUserName_returnsProfile() throws Exception {
        when(traineeService.getTrainee("faiq.azizzade")).thenReturn(TraineeResponseDTO.builder()
                .firstName("Faiq")
                .lastName("Azizzade")
                .build());

        mockMvc.perform(get("/trainees/faiq.azizzade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Faiq"));
    }

    @Test
    void updateTrainee_returnsUpdatedProfile() throws Exception {
        var request = TraineeRequestDTO.builder()
                .firstName("Faiq")
                .lastName("Azizzade")
                .isActive(true)
                .build();
        when(traineeService.updateTrainee(any(), eq("faiq.azizzade")))
                .thenReturn(TraineeResponseDTO.builder().userName("faiq.azizzade").build());

        mockMvc.perform(put("/trainees/faiq.azizzade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("faiq.azizzade"));
    }

    @Test
    void toggleTraineeStatus_returnsOk() throws Exception {
        mockMvc.perform(patch("/trainees/faiq.azizzade/status")
                        .param("isActive", "true"))
                .andExpect(status().isOk());

        verify(traineeService).changeStatus("faiq.azizzade", true);
    }

    @Test
    void deleteTrainee_returnsOk() throws Exception {
        mockMvc.perform(delete("/trainees/faiq.azizzade")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk());

        verify(traineeService).deleteTrainee("faiq.azizzade", "Bearer test-token");
    }

    @Test
    void updateTraineeTrainers_returnsUpdatedList() throws Exception {
        var request = TraineeTrainersUpdateRequestDTO.builder()
                .trainerUsernames(List.of("ilyas.azizzade"))
                .build();
        when(traineeService.updateTraineeTrainers(eq("faiq.azizzade"), eq(List.of("ilyas.azizzade"))))
                .thenReturn(TraineeResponseDTO.builder().userName("faiq.azizzade").build());

        mockMvc.perform(put("/trainees/faiq.azizzade/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("faiq.azizzade"));
    }

    @Test
    void getUnassignedTrainers_returnsList() throws Exception {
        when(traineeService.getUnassignedTrainers("faiq.azizzade"))
                .thenReturn(List.of(TrainerResponseDTO.builder().userName("ilyas.azizzade").build()));

        mockMvc.perform(get("/trainees/faiq.azizzade/available-trainers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userName").value("ilyas.azizzade"));
    }

    @Test
    void getTraineeTrainings_returnsTrainings() throws Exception {
        when(traineeService.getTraineeTrainings(eq("faiq.azizzade"),
                any(), any(), any(), any()))
                .thenReturn(TraineeResponseDTO.builder().userName("faiq.azizzade").build());

        mockMvc.perform(get("/trainees/faiq.azizzade/trainings")
                        .param("fromDate", "2024-01-01")
                        .param("toDate", "2024-12-31")
                        .param("trainerName", "Ilyas")
                        .param("trainingType", "Fitness"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("faiq.azizzade"));
    }
}
