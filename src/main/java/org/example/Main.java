package org.example;

import org.example.config.AppConfig;
import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.facade.GymCRM;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        GymCRM gymCRM = context.getBean(GymCRM.class);

        TrainerCreateRequestDTO trainerRequest = TrainerCreateRequestDTO.builder()
                .specializationName("Body Building")
                .lastName("Azizzade")
                .firstName("ilyas")
                .isActive(true)
                .build();

        TraineeCreateRequestDTO traineeRequest = TraineeCreateRequestDTO.builder()
                .dateOfBirth(java.time.LocalDate.of(1995, 5, 20))
                .address("123 Main St")
                .lastName("Smith")
                .firstName("John")
                .isActive(true)
                .build();

        var createdTrainer = gymCRM.createTrainer(trainerRequest);
        var createdTrainee = gymCRM.createTrainee(traineeRequest);

        LoginRequestDTO traineeAuth = LoginRequestDTO.builder()
                .username(createdTrainee.getUserName())
                .password(createdTrainee.getPassword())
                .build();

        LoginRequestDTO trainerAuth = LoginRequestDTO.builder()
                .username(createdTrainer.getUserName())
                .password(createdTrainer.getPassword())
                .build();

        if (!gymCRM.traineeMatcher(traineeAuth)) {
            System.out.println("Trainee login failed");
            return;
        }

        System.out.println("Trainee login successful: " + traineeAuth.getUsername());

        var trainer = gymCRM.getTrainer(trainerAuth, trainerAuth.getUsername());
        System.out.println("Trainer: " + trainer.getUser().getFirstName() + " "
                + trainer.getUser().getLastName());

        var trainee = gymCRM.getTrainee(traineeAuth, traineeAuth.getUsername());
        System.out.println("Trainee: " + trainee.getUser().getFirstName() + " "
                + trainee.getUser().getLastName());

        gymCRM.updateTrainer(trainerAuth, trainerAuth.getUsername(), TrainerRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .build());

        var training = gymCRM.createTraining(traineeAuth, TrainingRequestDTO.builder()
                .traineeUsername(traineeAuth.getUsername())
                .trainerUsername(trainerAuth.getUsername())
                .trainingDuration(60)
                .trainingName("Strength Training Session")
                .trainingDate(java.time.LocalDate.now())
                .trainingTypeName("Body Building")
                .build());

        System.out.println("Training created: " + training.getTrainingId());
        System.out.println("Trainee trainings: "
                + gymCRM.getTraineeTrainingsByCriteria(traineeAuth, traineeAuth.getUsername(), null).size());
        System.out.println("Trainer trainings: "
                + gymCRM.getTrainerTrainingsByCriteria(trainerAuth, trainerAuth.getUsername(),
                TrainingCriteria.builder().traineeName("John").build()).size());
        System.out.println("Unassigned trainers: "
                + gymCRM.getUnassignedTrainers(traineeAuth, traineeAuth.getUsername()).size());
    }
}
