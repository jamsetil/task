package org.example.service;

import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.model.Trainee;
import org.example.model.Trainer;

import java.util.List;

public interface TraineeService {
    TraineeResponseDTO createTrainee(TraineeCreateRequestDTO trainee);

    void updateTrainee(LoginRequestDTO auth, String username, TraineeRequestDTO requestDTO);

    void deleteTrainee(LoginRequestDTO auth, String username);

    Trainee getTrainee(LoginRequestDTO auth, String username);

    boolean matchTrainee(String username, String password);

    boolean changePassword(LoginRequestDTO auth, String oldPassword, String newPassword);

    Trainee changeStatus(LoginRequestDTO auth, String username);

    void updateTraineeTrainers(LoginRequestDTO auth, String username, List<String> trainerUsernames);

    List<Trainer> getUnassignedTrainers(LoginRequestDTO auth, String traineeUsername);
}
