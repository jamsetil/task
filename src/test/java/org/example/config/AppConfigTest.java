package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppConfigTest {

    private final AppConfig appConfig = new AppConfig();

    @Test
    void objectMapper_registersJavaTimeModule() throws Exception {
        ObjectMapper mapper = appConfig.objectMapper();

        LocalDate date = LocalDate.of(2024, 6, 15);
        String json = mapper.writeValueAsString(date);

        assertTrue(json.contains("2024"));
        assertEquals(date, mapper.readValue(json, LocalDate.class));
    }

    @Test
    void emf_createsGymCrmPersistenceUnit() {
        EntityManagerFactory mockEmf = mock(EntityManagerFactory.class);

        try (MockedStatic<Persistence> persistence = mockStatic(Persistence.class)) {
            persistence.when(() -> Persistence.createEntityManagerFactory("gym-crm"))
                    .thenReturn(mockEmf);

            EntityManagerFactory result = appConfig.emf();

            assertSame(mockEmf, result);
            persistence.verify(() -> Persistence.createEntityManagerFactory("gym-crm"));
        }
    }

    @Test
    void traineeStorage_returnsConcurrentMap() {
        Map<String, Trainee> storage = appConfig.traineeStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());

        var trainee = Trainee.builder().build();
        storage.put("key", trainee);

        assertEquals(1, storage.size());
        assertSame(trainee, storage.get("key"));
    }

    @Test
    void trainerStorage_returnsConcurrentMap() {
        Map<String, Trainer> storage = appConfig.trainerStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());

        var trainer = Trainer.builder().build();
        storage.put("key", trainer);

        assertEquals(1, storage.size());
        assertSame(trainer, storage.get("key"));
    }

    @Test
    void trainingStorage_returnsConcurrentMap() {
        Map<String, Training> storage = appConfig.trainingStorage();

        assertNotNull(storage);
        assertTrue(storage.isEmpty());

        var training = Training.builder().trainingName("Cardio").build();
        storage.put("key", training);

        assertEquals(1, storage.size());
        assertSame(training, storage.get("key"));
    }
}
