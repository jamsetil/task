package org.example.controller;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.dto.response.TrainingResponseDTO;
import org.example.filter.JwtAuthFilter;
import org.example.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void createTrainer_returnsCreatedProfile() throws Exception {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .specializationName("Body Building")
                .build();
        when(trainerService.createTrainer(any())).thenReturn(TrainerResponseDTO.builder()
                .userName("ilyas.azizzade")
                .password("secret1234")
                .token("jwt-token")
                .build());

        mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("ilyas.azizzade"));
    }

    @Test
    void getTrainerByUserName_returnsProfile() throws Exception {
        when(trainerService.getTrainer("ilyas.azizzade")).thenReturn(TrainerResponseDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .build());

        mockMvc.perform(get("/trainers/ilyas.azizzade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ilyas"));
    }

    @Test
    void updateTrainer_returnsUpdatedProfile() throws Exception {
        var request = TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .build();
        when(trainerService.updateTrainer(any(), eq("ilyas.azizzade")))
                .thenReturn(TrainerResponseDTO.builder().lastName("Azizzade").build());

        mockMvc.perform(put("/trainers/ilyas.azizzade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Azizzade"));
    }

    @Test
    void toggleTrainerStatus_returnsOk() throws Exception {
        mockMvc.perform(patch("/trainers/ilyas.azizzade/status")
                        .param("isActive", "false"))
                .andExpect(status().isOk());

        verify(trainerService).toggleTrainerStatus("ilyas.azizzade", false);
    }

    @Test
    void getTrainerTrainings_returnsTrainings() throws Exception {
        when(trainerService.getTrainerTrainings(eq("ilyas.azizzade"), any(), any(), any()))
                .thenReturn(TrainerResponseDTO.builder()
                        .trainingResponseDTOList(List.of(TrainingResponseDTO.builder()
                                .trainingName("Cardio")
                                .build()))
                        .build());

        mockMvc.perform(get("/trainers/ilyas.azizzade/trainings")
                        .param("traineeName", "Faiq Azizzade"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingResponseDTOList[0].trainingName").value("Cardio"));
    }
}
