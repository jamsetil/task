package org.example.config;

import org.example.config.AppConfig;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void contextLoadsAndBeansExist() {

        var context = new AnnotationConfigApplicationContext(AppConfig.class);

        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
        Map<String, Trainee> traineeTable = (Map<String, Trainee>) context.getBean("traineeTable");
        Map<String, Trainer> trainerTable = (Map<String, Trainer>) context.getBean("trainerTable");
        Map<String, Training> trainingTable = (Map<String, Training>) context.getBean("trainingTable");

        assertNotNull(objectMapper);
        assertNotNull(traineeTable);
        assertNotNull(trainerTable);
        assertNotNull(trainingTable);

        assertFalse(traineeTable.isEmpty());
        assertFalse(trainerTable.isEmpty());
        assertFalse(trainingTable.isEmpty());

        context.close();
    }
}