package org.example;

import org.example.config.AppConfig;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.facade.GymCRM;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {


    public static void main(String[] args) {


        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        GymCRM gymCRM = context.getBean(GymCRM.class);

        /*
        creating profiles  for step 1-2 and persisting it
         */
//        TrainerCreateRequestDTO requestDTO = TrainerCreateRequestDTO.builder()
//                .specializationName("Body Building")
//                .lastName("Azizzade")
//                .firstName("ilyas")
//                .userName("ilyas_Azizzade")
//                .isActive(true)
//                .build();
//
//
//        TraineeCreateRequestDTO traineeRequestDTO = TraineeCreateRequestDTO.builder()
//                .dateOfBirth(java.time.LocalDate.of(1995, 5, 20))
//                .address("123 Main St")
//                .lastName("Smith")
//                .firstName("John")
//                .userName("john_smith")
//                .isActive(true)
//                .build();
//
//        gymCRM.createTrainer(requestDTO);
//        gymCRM.createTrainee(traineeRequestDTO);

        /*
        selecting and username and password matching for step 3-6(including)
         */

        LoginRequestDTO loginRequestDTO = LoginRequestDTO.builder()
                .username("john_smith")
                .password("newPassword123")
                .build();

        if (!gymCRM.traineeMatcher(loginRequestDTO)) {
            System.out.println("Login failed for user: " + loginRequestDTO.getUsername());
            return;
        }

        System.out.println("Login successful for user: " + loginRequestDTO.getUsername());


        Trainer trainer = gymCRM.getTrainer("ilyas_Azizzade");

        System.out.println("Trainer found: " + trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName() +
                ", specialization: " + trainer.getSpecialization().getTrainingTypeName());

        Trainee trainee = gymCRM.getTrainee("john_smith");

        System.out.println("Trainee found: " + trainee.getUser().getFirstName() + " " + trainee.getUser().getLastName() +
                ", address: " + trainee.getAddress() + ", date of birth: " + trainee.getDateOfBirth());

        /*
        password changes for trainee and trainers step 7-8
         */

//        System.out.println(gymCRM.changeTraineePassword(
//                "john_smith",
//                "SLkCLWuDEc",
//                "newPassword123"));

//        System.out.println(gymCRM.changeTrainerPassword(
//                "ilyas_Azizzade",
//                "newPassword456",
//                "newPassword454"));

        /*
        update trainer/trainee profile for step 9-10
         */

            TrainerRequestDTO trainerUpdateRequest = TrainerRequestDTO.builder()

                    .firstName("Ilyasd Updated")
                    .lastName("Azizzaded Updated")
                    .build();

            gymCRM.updateTrainer("ilyas_Azizzade", trainerUpdateRequest);

//                TraineeRequestDTO traineeUpdateRequest = TraineeRequestDTO.builder()
//
//                        .firstName("John Updatedd")
//                        .lastName("Smith Updatedd")
//                        .address("456 New Address St")
//                        .dateOfBirth(java.time.LocalDate.of(1990, 1, 1))
//                        .build();
//
//                gymCRM.updateTrainee("john_smith", traineeUpdateRequest);

//            gymCRM.toggleTraineeStatus("john_smith");

//            gymCRM.trainerToggleStatus("ilyas_Azizzade");


//        System.out.println(gymCRM.getTrainingsByCriteria("john_smith", null));
        System.out.println(gymCRM.createTraining(TrainingRequestDTO.builder()
                .trainingDuration(60)
                .trainingName("Strength Training Session")
                .trainingDate(java.time.LocalDate.now())
                .trainingType(null).build()
        ) +"training created successfully");

    }
}