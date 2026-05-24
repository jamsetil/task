package org.example;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MainTest {

    @Mock
    private GymCRM gymCRM;

    @Test
    void runDemo_completesHappyPathAndPrintsSummary() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);

        stubSuccessfulFlow();

        Main.runDemo(gymCRM, printStream);

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Trainee login successful: john.smith"));
        assertTrue(console.contains("Trainer: Ilyas Azizzade"));
        assertTrue(console.contains("Trainee: John Smith"));
        assertTrue(console.contains("Training created: training-1"));
        assertTrue(console.contains("Trainee trainings: 2"));
        assertTrue(console.contains("Trainer trainings: 1"));
        assertTrue(console.contains("Unassigned trainers: 3"));

        verify(gymCRM).createTrainer(any(TrainerCreateRequestDTO.class));
        verify(gymCRM).createTrainee(any(TraineeCreateRequestDTO.class));
        verify(gymCRM).traineeMatcher(any(LoginRequestDTO.class));
        verify(gymCRM).getTrainer(any(LoginRequestDTO.class), eq("ilyas.azizzade"));
        verify(gymCRM).getTrainee(any(LoginRequestDTO.class), eq("john.smith"));
        verify(gymCRM).updateTrainer(any(LoginRequestDTO.class), eq("ilyas.azizzade"), any(TrainerRequestDTO.class));
        verify(gymCRM).createTraining(any(LoginRequestDTO.class), any(TrainingRequestDTO.class));
        verify(gymCRM).getTraineeTrainingsByCriteria(any(), eq("john.smith"), isNull());
        verify(gymCRM).getTrainerTrainingsByCriteria(any(), eq("ilyas.azizzade"), any(TrainingCriteria.class));
        verify(gymCRM).getUnassignedTrainers(any(), eq("john.smith"));
    }

    @Test
    void runDemo_stopsWhenTraineeLoginFails() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(output, true, StandardCharsets.UTF_8);

        when(gymCRM.createTrainer(any())).thenReturn(trainerResponse());
        when(gymCRM.createTrainee(any())).thenReturn(traineeResponse());
        when(gymCRM.traineeMatcher(any())).thenReturn(false);

        Main.runDemo(gymCRM, printStream);

        String console = output.toString(StandardCharsets.UTF_8);
        assertTrue(console.contains("Trainee login failed"));
        assertFalse(console.contains("Training created:"));

        verify(gymCRM, never()).getTrainer(any(), any());
        verify(gymCRM, never()).createTraining(any(), any());
    }

    @Test
    void main_bootstrapsContextAndRunsDemo() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        try (MockedConstruction<AnnotationConfigApplicationContext> ignored =
                     mockConstruction(AnnotationConfigApplicationContext.class, (mock, context) -> {
                         GymCRM mockGymCRM = mock(GymCRM.class);
                         when(mock.getBean(GymCRM.class)).thenReturn(mockGymCRM);
                         when(mockGymCRM.createTrainer(any())).thenReturn(trainerResponse());
                         when(mockGymCRM.createTrainee(any())).thenReturn(traineeResponse());
                         when(mockGymCRM.traineeMatcher(any())).thenReturn(false);
                     })) {
            Main.main(new String[]{});
        } finally {
            System.setOut(originalOut);
        }

        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Trainee login failed"));
    }

    private void stubSuccessfulFlow() {
        when(gymCRM.createTrainer(any())).thenReturn(trainerResponse());
        when(gymCRM.createTrainee(any())).thenReturn(traineeResponse());
        when(gymCRM.traineeMatcher(any())).thenReturn(true);

        when(gymCRM.getTrainer(any(), eq("ilyas.azizzade"))).thenReturn(
                Trainer.builder()
                        .user(User.builder().firstName("Ilyas").lastName("Azizzade").build())
                        .build());

        when(gymCRM.getTrainee(any(), eq("john.smith"))).thenReturn(
                Trainee.builder()
                        .user(User.builder().firstName("John").lastName("Smith").build())
                        .build());

        when(gymCRM.createTraining(any(), any())).thenReturn(
                Training.builder().trainingId("training-1").build());

        when(gymCRM.getTraineeTrainingsByCriteria(any(), eq("john.smith"), isNull()))
                .thenReturn(List.of(Training.builder().build(), Training.builder().build()));

        when(gymCRM.getTrainerTrainingsByCriteria(any(), eq("ilyas.azizzade"), any()))
                .thenReturn(List.of(Training.builder().build()));

        when(gymCRM.getUnassignedTrainers(any(), eq("john.smith")))
                .thenReturn(List.of(Trainer.builder().build(), Trainer.builder().build(), Trainer.builder().build()));
    }

    private TrainerResponseDTO trainerResponse() {
        return TrainerResponseDTO.builder()
                .userName("ilyas.azizzade")
                .password("trainer-pwd")
                .build();
    }

    private TraineeResponseDTO traineeResponse() {
        return TraineeResponseDTO.builder()
                .userName("john.smith")
                .password("trainee-pwd")
                .build();
    }
}
