package org.example.service;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;

import java.util.List;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer);

    void updateTrainer(String username, TrainerRequestDTO trainer);

    Trainer getTrainer(String username);

    Trainer toggleTrainerStatus(String username);

    List<Trainer> getAllTrainersWithNoTrainee(String traineeUsername);

    boolean matchCredentials(String username, String password);

    boolean changePassword(String username, String oldPassword, String newPassword);
}
