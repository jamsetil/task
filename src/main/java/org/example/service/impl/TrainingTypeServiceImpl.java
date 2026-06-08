package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.TrainingTypeResponseDTO;
import org.example.mapper.TrainingTypeMapper;
import org.example.repository.TrainingTypeRepository;
import org.example.service.TrainingTypeService;
import org.example.util.AuthValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TrainingTypeServiceImpl implements TrainingTypeService {

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;
    @Autowired
    private TrainingTypeMapper trainingTypeMapper;
    @Autowired
    private AuthValidator authValidator;

    @Override
    public List<TrainingTypeResponseDTO> getTrainingTypes(String username, String password) {
        authValidator.requireAuthentication(username, password);
        log.debug("Fetching all training types for username={}", username);
        return trainingTypeRepository.findAll().stream()
                .map(trainingTypeMapper::toResponseDTO)
                .toList();
    }
}
