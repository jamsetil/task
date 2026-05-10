package org.example.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class StorageInitializer {

    @Value("${app.seed.trainee-file}")
    private Resource traineeFile;

    @Value("${app.seed.trainer-file}")
    private Resource trainerFile;

    @Value("${app.seed.training-file}")
    private Resource trainingFile;

    private ObjectMapper objectMapper;

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final Map<String, Trainee> traineeTable;
    private final Map<String, Trainer> trainerTable;
    private final Map<String, Training> trainingTable;

    public StorageInitializer(
            Map<String, Trainee> traineeTable,
            Map<String, Trainer> trainerTable,
            Map<String, Training> trainingTable
    ) {
        this.traineeTable = traineeTable;
        this.trainerTable = trainerTable;
        this.trainingTable = trainingTable;
    }

    @PostConstruct
    public void init() throws IOException {

        log.info("Initializing in-memory storage from seed files...");

        try {
            Trainee trainee = objectMapper.readValue(traineeFile.getInputStream(), Trainee.class);
            Trainer trainer = objectMapper.readValue(trainerFile.getInputStream(), Trainer.class);
            Training training = objectMapper.readValue(trainingFile.getInputStream(), Training.class);

            traineeTable.put("1", trainee);
            trainerTable.put("1", trainer);
            trainingTable.put("1", training);

            log.info("Seed data loaded successfully for keys: trainee=1, trainer=1, training=1");

        } catch (IOException e) {
            log.error("Failed to initialize storage from seed files", e);
            throw e;
        }
    }
}