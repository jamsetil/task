package org.example.mapper;

import org.example.dto.response.TraineeResponseDTO;
import org.example.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = TrainerMapper.class)
public interface TraineeMapper {

    @Mapping(target = "userName", source = "user.userName")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "dateOfBirth", expression = "java(trainee.getDateOfBirth() != null ? trainee.getDateOfBirth().toString() : null)")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "isActive", source = "user.isActive")
    @Mapping(target = "trainerList", source = "trainers")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "trainerName", ignore = true)
    TraineeResponseDTO toResponseDTO(Trainee trainee);
}
