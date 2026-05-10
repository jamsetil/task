package org.example.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageInitializerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Resource traineeFile;

    @Mock
    private Resource trainerFile;

    @Mock
    private Resource trainingFile;

    @Mock
    private InputStream traineeStream;

    @Mock
    private InputStream trainerStream;

    @Mock
    private InputStream trainingStream;

    private Map<String, Trainee> traineeTable;
    private Map<String, Trainer> trainerTable;
    private Map<String, Training> trainingTable;

    private StorageInitializer initializer;

    @BeforeEach
    void setUp() throws Exception {

        traineeTable = new HashMap<>();
        trainerTable = new HashMap<>();
        trainingTable = new HashMap<>();

        initializer = new StorageInitializer(
                traineeTable,
                trainerTable,
                trainingTable
        );

        initializer.setObjectMapper(objectMapper);

        // inject @Value fields manually or constructor injection
        Field traineeField = StorageInitializer.class.getDeclaredField("traineeFile");
        traineeField.setAccessible(true);
        traineeField.set(initializer, traineeFile);

        Field trainerField = StorageInitializer.class.getDeclaredField("trainerFile");
        trainerField.setAccessible(true);
        trainerField.set(initializer, trainerFile);

        Field trainingField = StorageInitializer.class.getDeclaredField("trainingFile");
        trainingField.setAccessible(true);
        trainingField.set(initializer, trainingFile);

        when(traineeFile.getInputStream()).thenReturn(traineeStream);
        when(trainerFile.getInputStream()).thenReturn(trainerStream);
        when(trainingFile.getInputStream()).thenReturn(trainingStream);
    }

    @Test
    void shouldLoadDataIntoStorage() throws Exception {

        Trainee trainee = Trainee.builder()
                .userId("t1")
                .firstName("ilyas")
                .build();

        Trainer trainer = Trainer.builder()
                .userId("tr1")
                .firstName("nazim")
                .build();

        Training training = Training.builder()
                .trainingId("trn1")
                .trainingName("Yoga")
                .build();

        when(objectMapper.readValue(traineeStream, Trainee.class))
                .thenReturn(trainee);

        when(objectMapper.readValue(trainerStream, Trainer.class))
                .thenReturn(trainer);

        when(objectMapper.readValue(trainingStream, Training.class))
                .thenReturn(training);

        initializer.init();

        assertEquals(1, traineeTable.size());
        assertEquals(1, trainerTable.size());
        assertEquals(1, trainingTable.size());

        assertEquals(trainee, traineeTable.get("1"));
        assertEquals(trainer, trainerTable.get("1"));
        assertEquals(training, trainingTable.get("1"));

        verify(objectMapper).readValue(traineeStream, Trainee.class);
        verify(objectMapper).readValue(trainerStream, Trainer.class);
        verify(objectMapper).readValue(trainingStream, Training.class);
    }
}