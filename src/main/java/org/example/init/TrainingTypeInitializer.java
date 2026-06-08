package org.example.init;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainingTypeInitializer {

    private static final List<String> DEFAULT_TYPES = List.of(
            "Fitness",
            "Yoga",
            "Body Building",
            "Cardio",
            "Strength"
    );

    private final TrainingTypeRepository trainingTypeRepository;

    @PostConstruct
    @Transactional
    public void init() {
        for (String typeName : DEFAULT_TYPES) {
            if (trainingTypeRepository.findByTrainingTypeName(typeName).isEmpty()) {
                trainingTypeRepository.save(TrainingType.builder().trainingTypeName(typeName).build());
                log.info("Seeded training type: {}", typeName);
            }
        }
    }
}
