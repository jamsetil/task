package org.example.mapper;

import org.example.model.TrainingType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingTypeMapperTest {

    private final TrainingTypeMapper mapper = new TrainingTypeMapperImpl();

    @Test
    void toResponseDTO_mapsFields() {
        var dto = mapper.toResponseDTO(
                TrainingType.builder().trainingTypeId(3L).trainingTypeName("Yoga").build());

        assertEquals("3", dto.getTrainingTypeId());
        assertEquals("Yoga", dto.getTrainingType());
    }
}
