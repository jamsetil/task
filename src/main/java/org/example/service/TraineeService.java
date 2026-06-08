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

    TraineeResponseDTO updateTrainee(TraineeRequestDTO requestDTO, String username, String password);

    void deleteTrainee(String username, String password);

    TraineeResponseDTO getTrainee(String username, String password);

    Trainee changeStatus(String username, boolean isActive, String password);

    TraineeResponseDTO updateTraineeTrainers(String username, List<String> trainerUsernames, String password);

    List<TrainerResponseDTO> getUnassignedTrainers(String traineeUsername, String password);

    TraineeResponseDTO getTraineeTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                           String trainerName, String trainingType);
}
