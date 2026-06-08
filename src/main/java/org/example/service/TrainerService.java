package org.example.service;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;

import java.time.LocalDate;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer);

    TrainerResponseDTO updateTrainer(TrainerRequestDTO trainer, String username, String password);

    TrainerResponseDTO getTrainer(String username, String password);

    Trainer toggleTrainerStatus(String username, boolean isActive, String password);

    TrainerResponseDTO getTrainerTrainings(String username, String password, LocalDate fromDate, LocalDate toDate,
                                           String traineeName);
}
