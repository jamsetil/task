package org.example.dao;

import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDAO {

    private Map<String, Training> trainingTable;

    @Autowired
    public void setTrainingTable(Map<String, Training> trainingTable) {
        this.trainingTable = trainingTable;
    }

    public Training save(Training training) {
        return trainingTable.put(training.getTrainingId(), training);
    }

    public Optional<Training> find(String trainingId) {
        return Optional.ofNullable(trainingTable.get(trainingId));
    }
}
