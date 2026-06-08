package org.example.mapper;

import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "specializationId", source = "specialization.trainingTypeId")
    @Mapping(target = "traineeResponseDTOList", source = "trainees")
    @Mapping(target = "trainingResponseDTOList", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainerId", ignore = true)
    TrainerResponseDTO toResponseDTO(Trainer trainer);

    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "dateOfBirth", expression = "java(trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().toString() : null)")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainerList", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainerName", ignore = true)
    TraineeResponseDTO toTraineeDto(Trainee trainee);
}
