package org.example.service.impl;

import org.example.dao.TrainerDAO;
import org.example.dao.TrainingTypeDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Trainer;
import org.example.model.TrainingType;
import org.example.model.base.User;
import org.example.security.AuthValidator;
import org.example.util.CredentialGenerator;
import org.example.validation.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDAO trainerDAO;
    @Mock
    private TrainingTypeDAO trainingTypeDAO;
    @Mock
    private CredentialGenerator generator;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private RequestValidator requestValidator;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    void createTrainer_generatesCredentialsAndLooksUpTrainingType() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .specializationName("Body Building")
                .build();

        when(generator.generateUsername("Ilyas", "Azizzade")).thenReturn("ilyas.azizzade");
        when(generator.generatePassword()).thenReturn("secret1234");
        when(trainingTypeDAO.findByName("Body Building"))
                .thenReturn(Optional.of(TrainingType.builder().trainingTypeName("Body Building").build()));
        when(trainerDAO.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = trainerService.createTrainer(request);

        assertEquals("ilyas.azizzade", response.getUserName());
        assertEquals("secret1234", response.getPassword());
        verify(requestValidator).validate(request);
        verify(trainerDAO).save(any(Trainer.class));
    }

    @Test
    void createTrainer_unknownTrainingType_throws() {
        var request = TrainerCreateRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .isActive(true)
                .specializationName("Unknown")
                .build();

        when(generator.generateUsername(any(), any())).thenReturn("ilyas.azizzade");
        when(generator.generatePassword()).thenReturn("secret1234");
        when(trainingTypeDAO.findByName("Unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.createTrainer(request));
    }

    @Test
    void getTrainer_requiresAuthentication() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        var trainer = Trainer.builder()
                .user(User.builder().userName("trainer").build())
                .build();

        when(trainerDAO.find("trainer")).thenReturn(Optional.of(trainer));

        assertSame(trainer, trainerService.getTrainer(auth, "trainer"));
        verify(authValidator).requireTrainer(auth, "trainer");
    }

    @Test
    void toggleTrainerStatus_delegatesToDao() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        var trainer = Trainer.builder().user(User.builder().userName("trainer").build()).build();
        when(trainerDAO.toggleStatus("trainer")).thenReturn(trainer);

        assertSame(trainer, trainerService.toggleTrainerStatus(auth, "trainer"));
    }

    @Test
    void updateTrainer_updatesExistingEntity() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        var user = User.builder().userName("trainer").firstName("Old").lastName("Name").build();
        var trainer = Trainer.builder().user(user).build();
        var request = TrainerRequestDTO.builder().firstName("New").build();

        when(trainerDAO.find("trainer")).thenReturn(Optional.of(trainer));
        when(trainerDAO.update("trainer", trainer)).thenReturn(trainer);

        trainerService.updateTrainer(auth, "trainer", request);

        assertEquals("New", trainer.getUser().getFirstName());
        verify(trainerDAO).update("trainer", trainer);
    }

    @Test
    void changePassword_success_returnsTrue() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainerDAO.changePassword("trainer", "old", "new")).thenReturn(true);

        assertTrue(trainerService.changePassword(auth, "old", "new"));
    }

    @Test
    void changePassword_failure_returnsFalse() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainerDAO.changePassword("trainer", "old", "new")).thenReturn(false);

        assertFalse(trainerService.changePassword(auth, "old", "new"));
    }

    @Test
    void getTrainer_notFound_throws() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainerDAO.find("trainer")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> trainerService.getTrainer(auth, "trainer"));
    }
}
