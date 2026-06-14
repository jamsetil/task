package org.example.service;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;

import java.time.LocalDate;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerCreateRequestDTO trainer);

    TrainerResponseDTO updateTrainer(TrainerRequestDTO trainer, String username);

    TrainerResponseDTO getTrainer(String username);

    Trainer toggleTrainerStatus(String username, boolean isActive);

    TrainerResponseDTO getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                           String traineeName);
}
