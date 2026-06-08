package org.example.mapper;

import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    @Mapping(target = "trainingTypeId", expression = "java(String.valueOf(trainingType.getTrainingTypeId()))")
    @Mapping(target = "trainingType", source = "trainingTypeName")
    TrainingTypeResponseDTO toResponseDTO(TrainingType trainingType);
}
