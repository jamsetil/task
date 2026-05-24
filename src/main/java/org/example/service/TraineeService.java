package org.example.service;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.model.Trainee;

import java.util.List;


public interface TraineeService {
    TraineeResponseDTO createTrainee(TraineeCreateRequestDTO trainee);

    void updateTrainee(String userId, TraineeRequestDTO requestDTO);

    void deleteTrainee(String userId);

    Trainee getTrainee(String userUd);

    boolean matchTrainee(String username, String password);

    boolean changePassword(String username, String oldPassword, String newPassword);

    Trainee changeStatus(String username);

    void updateTraineeToTrainer(String username, List<String> trainerUsernames);
}
