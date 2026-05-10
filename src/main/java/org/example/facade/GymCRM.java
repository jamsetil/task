package org.example.facade;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;
import org.example.model.Training;
import org.example.service.TraineeService;
import org.example.service.TrainerService;
import org.example.service.TrainingService;
import org.springframework.stereotype.Component;

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

    /** Training
     * Create, get
     * @param trainer
     * @return
     */
    public Training createTraining(TrainingRequestDTO trainer) {
        return trainingService.createTraining(trainer);
    }

    public Training getTraining(String trainingId) {
        return trainingService.getTraining(trainingId);
    }


    /** Trainer
     * Create, update, get
     * @param trainer
     * @return
     */
    public TrainerResponseDTO createTrainer(TrainerRequestDTO trainer) {
        return trainerService.createTrainer(trainer);
    }

    public void updateTrainer(String username, TrainerRequestDTO trainer) {
        trainerService.updateTrainer(username, trainer);
    }

    public  TrainerResponseDTO getTrainer(String id) {
        return trainerService.getTrainer(id);
    }


    /** Trainee
     * Create, update, get, delete
     * @param requestDTO
     * @return
     */
    public TraineeResponseDTO createTrainee(TraineeRequestDTO requestDTO) {
        return traineeService.createTrainee(requestDTO);
    }

    public void updateTrainee(String userId, TraineeRequestDTO requestDTO) {
        traineeService.updateTrainee(userId, requestDTO);
    }

    public void deleteTrainee(String username) {
        traineeService.deleteTrainee(username);
    }

    public Trainee getTrainee(String username) {
        return traineeService.getTrainee(username);
    }

}
