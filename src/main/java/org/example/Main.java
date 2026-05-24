package org.example;

import org.example.config.AppConfig;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.facade.GymCRM;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.base.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(AppConfig.class)) {
            runDemo(context.getBean(GymCRM.class), System.out);
        }
    }

    static void runDemo(GymCRM gymCRM, PrintStream out) {
        TrainerCreateRequestDTO trainerRequest = TrainerCreateRequestDTO.builder()
                .specializationName("Body Building")
                .lastName("Azizzade")
                .firstName("ilyas")
                .isActive(true)
                .build();

        TraineeCreateRequestDTO traineeRequest = TraineeCreateRequestDTO.builder()
                .dateOfBirth(LocalDate.of(1995, 5, 20))
                .address("123 Main St")
                .lastName("Smith")
                .firstName("John")
                .isActive(true)
                .build();

        TrainerResponseDTO createdTrainer = gymCRM.createTrainer(trainerRequest);
        TraineeResponseDTO createdTrainee = gymCRM.createTrainee(traineeRequest);

        LoginRequestDTO traineeAuth = LoginRequestDTO.builder()
                .username(createdTrainee.getUserName())
                .password(createdTrainee.getPassword())
                .build();

        LoginRequestDTO trainerAuth = LoginRequestDTO.builder()
                .username(createdTrainer.getUserName())
                .password(createdTrainer.getPassword())
                .build();

        if (!gymCRM.traineeMatcher(traineeAuth)) {
            out.println("Trainee login failed");
            return;
        }

        out.println("Trainee login successful: " + traineeAuth.getUsername());

        Trainer trainer = gymCRM.getTrainer(trainerAuth, trainerAuth.getUsername());
        out.println("Trainer: " + trainer.getUser().getFirstName() + " "
                + trainer.getUser().getLastName());

        Trainee trainee = gymCRM.getTrainee(traineeAuth, traineeAuth.getUsername());
        out.println("Trainee: " + trainee.getUser().getFirstName() + " "
                + trainee.getUser().getLastName());

        gymCRM.updateTrainer(trainerAuth, trainerAuth.getUsername(), TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .build());

        Training training = gymCRM.createTraining(traineeAuth, TrainingRequestDTO.builder()
                .traineeUsername(traineeAuth.getUsername())
                .trainerUsername(trainerAuth.getUsername())
                .trainingDuration(60)
                .trainingName("Strength Training Session")
                .trainingDate(LocalDate.now())
                .trainingTypeName("Body Building")
                .build());

        out.println("Training created: " + training.getTrainingId());
        out.println("Trainee trainings: "
                + gymCRM.getTraineeTrainingsByCriteria(traineeAuth, traineeAuth.getUsername(), null).size());
        out.println("Trainer trainings: "
                + gymCRM.getTrainerTrainingsByCriteria(trainerAuth, trainerAuth.getUsername(),
                TrainingCriteria.builder().traineeName("John").build()).size());
        out.println("Unassigned trainers: "
                + gymCRM.getUnassignedTrainers(traineeAuth, traineeAuth.getUsername()).size());
    }
}
