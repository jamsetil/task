package org.example.service;

import org.example.dto.response.TrainingTypeResponseDTO;

import java.util.List;

public interface TrainingTypeService {

    List<TrainingTypeResponseDTO> getTrainingTypes();
}
