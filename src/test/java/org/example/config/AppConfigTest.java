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


}
