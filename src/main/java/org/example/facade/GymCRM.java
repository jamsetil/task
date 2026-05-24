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

    public List<Training> getTrainingsByCriteria(String traineeUsername, TrainingCriteria criteria) {
        return trainingService.getAllTrainingsByTraineeUsername(traineeUsername, criteria);
    }



    /** Training
     * Create, get
     * @param trainer
     * @return
     */
    public Training createTraining(TrainingRequestDTO trainer) {
        return trainingService.createTraining(trainer);
    }

    public void updateTraineesTraining(String trainingId, List<String> traineeUsernames) {
        traineeService.updateTraineeToTrainer(trainingId, traineeUsernames);
    }

    public Training getTraining(String trainingId) {
        return trainingService.getTraining(trainingId);
    }


    /** Trainer
     * Create, update, get
     * @param trainer
     * @return
     */
    public TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer) {
        return trainerService.createTrainer(trainer);
    }

    public void updateTrainer(String username, TrainerRequestDTO trainer) {
        trainerService.updateTrainer(username, trainer);
    }

    public Trainer getTrainer(String username) {
        return trainerService.getTrainer(username);
    }


    /** Trainee
     * Create, update, get, delete
     * @param requestDTO
     * @return
     */
    public TraineeResponseDTO createTrainee(TraineeCreateRequestDTO requestDTO) {
        return traineeService.createTrainee(requestDTO);
    }

    public void updateTrainee(String userId, TraineeRequestDTO requestDTO) {
        traineeService.updateTrainee(userId, requestDTO);
    }

    public Trainee toggleTraineeStatus(String username) {
        return traineeService.changeStatus(username);
    }

    public Trainer trainerToggleStatus(String username) {
        return trainerService.toggleTrainerStatus(username);
    }

    public void deleteTrainee(String username) {

        traineeService.deleteTrainee(username);
    }

    public Trainee getTrainee(String username) {
        return traineeService.getTrainee(username);
    }

    public boolean changeTraineePassword(String username, String oldPassword, String newPassword) {
        return traineeService.changePassword(username, oldPassword, newPassword);
    }

    public boolean changeTrainerPassword(String username, String oldPassword, String newPassword) {
        return trainerService.changePassword(username, oldPassword, newPassword);
    }

}
