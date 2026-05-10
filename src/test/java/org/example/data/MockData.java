package org.example.data;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;

public class MockData {
    public static TraineeRequestDTO getTraineeRequestDTO() {
        return TraineeRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .address("Baku")
                .trainings(List.of(MockData.getTraining()))
                .dateOfBirth(LocalDate.of(1999, 9, 16))
                .build();
    }

    public static Trainee getTrainee() {
        return Trainee.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .address("Baku")
                .password("examplePassword")
                .userName("Ilyas.Azizzade")
                .trainings(List.of(MockData.getTraining()))
                .dateOfBirth(LocalDate.of(1999, 9, 16))
                .userId("12")
                .build();
    }

    public static TrainerRequestDTO getTrainerRequestDTO() {
        return TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .wantsPasswordChange(false)
                .specialization("Fitness")
                .trainings(List.of(MockData.getTraining()))
                .build();
    }

    public static Trainer getTrainer () {
        return Trainer.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .password("examplePassword")
                .userName("Ilyas.Azizzade")
                .specialization("Fitness")
                .trainings(List.of(MockData.getTraining()))
                .userId("12")
                .build();
    }

    public static TrainingRequestDTO getTrainingRequestDTO() {
        return TrainingRequestDTO.builder()
                .trainingName("Cardio")
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2025, 1, 1))
                .traineeId("trainee-1")
                .trainerId("trainer-1")
                .build();
    }

    public static Training getTraining(){
        return Training.builder()
                .trainingId("1")
                .trainingName("Cardio")
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2025, 1, 1))
                .traineeId("trainee-1")
                .trainerId("trainer-1")
                .build();
    }
}
