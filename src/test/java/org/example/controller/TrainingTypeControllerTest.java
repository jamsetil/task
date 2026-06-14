package org.example.controller;

import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.filter.JwtAuthFilter;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingTypeController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void getTrainingTypes_returnsList() throws Exception {
        when(trainingTypeService.getTrainingTypes()).thenReturn(List.of(
                new TrainingTypeResponseDTO("1", "Yoga"),
                new TrainingTypeResponseDTO("2", "Cardio")
        ));

        mockMvc.perform(get("/training-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value("Yoga"))
                .andExpect(jsonPath("$[1].trainingTypeId").value("2"));

        verify(trainingTypeService).getTrainingTypes();
    }
}
