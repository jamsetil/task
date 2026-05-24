package org.example.facade;

import org.example.dto.TrainingCriteria;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymCRM {

    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;

    public GymCRM(TrainingService trainingService, TraineeService traineeService, TrainerService trainerService) {
        this.trainingService = trainingService;
        this.traineeService = traineeService;
        this.trainerService = trainerService;
    }

    public boolean trainerMatcher(LoginRequestDTO loginRequest) {
        return trainerService.matchCredentials(loginRequest.getUsername(), loginRequest.getPassword());
    }

    public boolean traineeMatcher(LoginRequestDTO loginRequest) {
        return traineeService.matchTrainee(loginRequest.getUsername(), loginRequest.getPassword());
    }

    public TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer) {
        return trainerService.createTrainer(trainer);
    }

    public TraineeResponseDTO createTrainee(TraineeCreateRequestDTO requestDTO) {
        return traineeService.createTrainee(requestDTO);
    }

    public Trainer getTrainer(LoginRequestDTO auth, String username) {
        return trainerService.getTrainer(auth, username);
    }

    public Trainee getTrainee(LoginRequestDTO auth, String username) {
        return traineeService.getTrainee(auth, username);
    }

    public void updateTrainer(LoginRequestDTO auth, String username, TrainerRequestDTO trainer) {
        trainerService.updateTrainer(auth, username, trainer);
    }

    public void updateTrainee(LoginRequestDTO auth, String username, TraineeRequestDTO requestDTO) {
        traineeService.updateTrainee(auth, username, requestDTO);
    }

    public Trainer trainerToggleStatus(LoginRequestDTO auth, String username) {
        return trainerService.toggleTrainerStatus(auth, username);
    }

    public Trainee toggleTraineeStatus(LoginRequestDTO auth, String username) {
        return traineeService.changeStatus(auth, username);
    }

    public void deleteTrainee(LoginRequestDTO auth, String username) {
        traineeService.deleteTrainee(auth, username);
    }

    public boolean changeTraineePassword(LoginRequestDTO auth, String oldPassword, String newPassword) {
        return traineeService.changePassword(auth, oldPassword, newPassword);
    }

    public boolean changeTrainerPassword(LoginRequestDTO auth, String oldPassword, String newPassword) {
        return trainerService.changePassword(auth, oldPassword, newPassword);
    }

    public List<Training> getTraineeTrainingsByCriteria(
            LoginRequestDTO auth,
            String traineeUsername,
            TrainingCriteria criteria
    ) {
        return trainingService.getAllTrainingsByTraineeUsername(auth, traineeUsername, criteria);
    }

    public List<Training> getTrainerTrainingsByCriteria(
            LoginRequestDTO auth,
            String trainerUsername,
            TrainingCriteria criteria
    ) {
        return trainingService.getAllTrainingsByTrainerUsername(auth, trainerUsername, criteria);
    }

    public Training createTraining(LoginRequestDTO auth, TrainingRequestDTO request) {
        return trainingService.createTraining(auth, request);
    }

    public Training getTraining(LoginRequestDTO auth, String trainingId) {
        return trainingService.getTraining(auth, trainingId);
    }

    public List<Trainer> getUnassignedTrainers(LoginRequestDTO auth, String traineeUsername) {
        return traineeService.getUnassignedTrainers(auth, traineeUsername);
    }

    public void updateTraineeTrainers(LoginRequestDTO auth, String traineeUsername, List<String> trainerUsernames) {
        traineeService.updateTraineeTrainers(auth, traineeUsername, trainerUsernames);
    }
}
