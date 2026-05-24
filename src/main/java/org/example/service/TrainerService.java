package org.example.service;

import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer);

    void updateTrainer(LoginRequestDTO auth, String username, TrainerRequestDTO trainer);

    Trainer getTrainer(LoginRequestDTO auth, String username);

    Trainer toggleTrainerStatus(LoginRequestDTO auth, String username);

    boolean matchCredentials(String username, String password);

    boolean changePassword(LoginRequestDTO auth, String oldPassword, String newPassword);
}
