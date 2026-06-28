package org.example.service;

import org.example.dto.request.TraineeRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.response.TraineeResponseDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    TraineeResponseDTO createTrainee(TraineeCreateRequestDTO trainee);

    TraineeResponseDTO updateTrainee(TraineeRequestDTO requestDTO, String username);

    void deleteTrainee(String username, String authorizationHeader);

    TraineeResponseDTO getTrainee(String username);

    Trainee changeStatus(String username, boolean isActive);

    TraineeResponseDTO updateTraineeTrainers(String username, List<String> trainerUsernames);

    List<TrainerResponseDTO> getUnassignedTrainers(String traineeUsername);

    TraineeResponseDTO getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                           String trainerName, String trainingType);
}
