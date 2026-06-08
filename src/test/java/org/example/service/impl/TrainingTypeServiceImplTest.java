package org.example.service.impl;

import org.example.exception.AuthenticationException;
import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepository;
import org.example.util.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private AuthValidator authValidator;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @BeforeEach
    void wireMapper() throws Exception {
        Field field = trainingTypeService.getClass().getDeclaredField("trainingTypeMapper");
        field.setAccessible(true);
        field.set(trainingTypeService, new org.example.mapper.TrainingTypeMapperImpl());
    }

    @Test
    void getTrainingTypes_mapsEntitiesToDto() {
        when(trainingTypeRepository.findAll()).thenReturn(java.util.List.of(
                TrainingType.builder().trainingTypeId(1L).trainingTypeName("Yoga").build(),
                TrainingType.builder().trainingTypeId(2L).trainingTypeName("Cardio").build()
        ));

        var result = trainingTypeService.getTrainingTypes("john", "pwd");

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getTrainingTypeId());
        assertEquals("Yoga", result.get(0).getTrainingType());
        verify(authValidator).requireAuthentication("john", "pwd");
    }

    @Test
    void getTrainingTypes_invalidAuth_throws() {
        doThrow(new AuthenticationException("Invalid username or password"))
                .when(authValidator).requireAuthentication("john", "bad");

        assertThrows(AuthenticationException.class,
                () -> trainingTypeService.getTrainingTypes("john", "bad"));
        verify(trainingTypeRepository, never()).findAll();
    }
}
