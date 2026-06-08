package org.example.controller;

import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrainingTypeController.class)
class TrainingTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypeService trainingTypeService;

    @Test
    void getTrainingTypes_returnsList() throws Exception {
        when(trainingTypeService.getTrainingTypes("john", "pwd")).thenReturn(List.of(
                new TrainingTypeResponseDTO("1", "Yoga"),
                new TrainingTypeResponseDTO("2", "Cardio")
        ));

        mockMvc.perform(get("/training-types")
                        .param("username", "john")
                        .param("password", "pwd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingType").value("Yoga"))
                .andExpect(jsonPath("$[1].trainingTypeId").value("2"));

        verify(trainingTypeService).getTrainingTypes("john", "pwd");
    }
}
