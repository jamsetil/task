package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan(basePackages = "org.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean("traineeTable")
    public Map<String, Trainee> traineeStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean("trainerTable")
    public Map<String, Trainer> trainerStorage() {
        return new ConcurrentHashMap<>();
    }

    @Bean("trainingTable")
    public Map<String, Training> trainingStorage() {
        return new ConcurrentHashMap<>();
    }
}
