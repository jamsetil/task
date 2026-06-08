package org.example.service.impl;

import org.example.dao.TrainingTypeDAO;
import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.mapper.TrainingTypeMapperImpl;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeDAO trainingTypeDAO;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void wireMapper() throws Exception {
        Field field = trainingTypeService.getClass().getDeclaredField("trainingTypeMapper");
        field.setAccessible(true);
        field.set(trainingTypeService, new TrainingTypeMapperImpl());
    }

    @Test
    void getTrainingTypes_mapsEntitiesToDto() {
        when(trainingTypeDAO.getAllTrainingTypes()).thenReturn(List.of(
                TrainingType.builder().trainingTypeId(1L).trainingTypeName("Yoga").build(),
                TrainingType.builder().trainingTypeId(2L).trainingTypeName("Cardio").build()
        ));

        List<TrainingTypeResponseDTO> result = trainingTypeService.getTrainingTypes();

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getTrainingTypeId());
        assertEquals("Yoga", result.get(0).getTrainingType());
        assertEquals("2", result.get(1).getTrainingTypeId());
        assertEquals("Cardio", result.get(1).getTrainingType());
    }
}
