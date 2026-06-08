package org.example.service.impl;

import org.example.dao.TrainingTypeDAO;
import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.mapper.TrainingTypeMapper;
import org.example.service.TrainingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {

    @Autowired
    private TrainingTypeDAO trainingTypeDAO;
    @Autowired
    private TrainingTypeMapper trainingTypeMapper;

    @Override
    public List<TrainingTypeResponseDTO> getTrainingTypes() {
        return trainingTypeDAO.getAllTrainingTypes().stream()
                .map(trainingTypeMapper::toResponseDTO)
                .toList();
    }
}
