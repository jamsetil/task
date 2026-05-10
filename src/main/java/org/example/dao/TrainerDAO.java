package org.example.dao;

import org.example.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDAO {

    private  Map<String, Trainer> trainerTable;

    @Autowired
    public void setTrainerTable(Map<String, Trainer> trainerTable) {
        this.trainerTable = trainerTable;
    }

    public Trainer save(Trainer trainer) {
        return trainerTable.put(trainer.getUserId(), trainer);
    }

    public Optional<Trainer> find(String userId) {
        return Optional.ofNullable(trainerTable.get(userId));
    }

    public Trainer update (String userId, Trainer trainer) {
        return trainerTable.put(userId, trainer);
    }
}
