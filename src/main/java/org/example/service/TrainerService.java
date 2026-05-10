package org.example.service;

import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.response.TrainerResponseDTO;
import org.example.model.Trainer;

public interface TrainerService {
    TrainerResponseDTO createTrainer(TrainerRequestDTO trainer);

    void updateTrainer(String username, TrainerRequestDTO trainer);

    TrainerResponseDTO getTrainer(String id);
}
