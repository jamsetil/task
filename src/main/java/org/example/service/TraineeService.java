package org.example.service;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.model.Trainee;

import java.util.Optional;


public interface TraineeService {
    TraineeResponseDTO createTrainee(TraineeRequestDTO trainee);

    void updateTrainee(String userId, TraineeRequestDTO requestDTO);

    void deleteTrainee(String userId);

    Trainee getTrainee(String userUd);
}
