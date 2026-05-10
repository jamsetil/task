package org.example.dao;

import org.example.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDAO {

    private Map<String, Trainee> traineeTable;

    @Autowired
    public void setTraineeTable(Map<String, Trainee> traineeTable) {
        this.traineeTable = traineeTable;
    }

    public void save(Trainee trainee) {
        traineeTable.put(trainee.getUserId(), trainee);
    }

    public Optional<Trainee> find(String userId) {
        return Optional.ofNullable(traineeTable.get(userId));
    }

    public void delete(String userId) {
        traineeTable.remove(userId);
    }
}