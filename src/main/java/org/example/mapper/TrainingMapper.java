package org.example.mapper;

import org.example.dto.response.TrainingResponseDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    @Mapping(target = "trainingDate", expression = "java(String.valueOf(training.getTrainingDate()))")
    @Mapping(target = "trainerName", expression = "java(fullName(training.getTrainer()))")
    @Mapping(target = "traineeName", expression = "java(fullName(training.getTrainee()))")
    @Mapping(target = "trainingType", expression = "java(trainingTypeName(training))")
    TrainingResponseDTO toResponseDTO(Training training);

    default String fullName(Trainer trainer) {
        if (trainer == null || trainer.getUser() == null) {
            return null;
        }
        return trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
    }

    default String fullName(Trainee trainee) {
        if (trainee == null || trainee.getUser() == null) {
            return null;
        }
        return trainee.getUser().getFirstName() + " " + trainee.getUser().getLastName();
    }

    default String trainingTypeName(Training training) {
        if (training.getTrainingType() == null) {
            return null;
        }
        return training.getTrainingType().getTrainingTypeName();
    }
}
