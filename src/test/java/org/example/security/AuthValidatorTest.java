package org.example.security;

import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.exception.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthValidatorTest {

    @Mock
    private TraineeDAO traineeDAO;
    @Mock
    private TrainerDAO trainerDAO;

    @InjectMocks
    private AuthValidator authValidator;

    @Test
    void requireTrainee_validCredentials_passes() {
        var auth = LoginRequestDTO.builder().username("john").password("pwd").build();
        when(traineeDAO.matchTrainee("john", "pwd")).thenReturn(true);

        assertDoesNotThrow(() -> authValidator.requireTrainee(auth, "john"));
    }

    @Test
    void requireTrainee_nullAuth_throws() {
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainee(null, "john"));
    }

    @Test
    void requireTrainee_nullUsername_throws() {
        var auth = LoginRequestDTO.builder().username(null).password("pwd").build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainee(auth, "john"));
    }

    @Test
    void requireTrainee_nullPassword_throws() {
        var auth = LoginRequestDTO.builder().username("john").password(null).build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainee(auth, "john"));
    }

    @Test
    void requireTrainee_usernameMismatch_throws() {
        var auth = LoginRequestDTO.builder().username("john").password("pwd").build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainee(auth, "other"));
    }

    @Test
    void requireTrainee_wrongPassword_throws() {
        var auth = LoginRequestDTO.builder().username("john").password("bad").build();
        when(traineeDAO.matchTrainee("john", "bad")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainee(auth, "john"));
    }

    @Test
    void requireTrainer_validCredentials_passes() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        when(trainerDAO.matchTrainer("trainer", "pwd")).thenReturn(true);

        assertDoesNotThrow(() -> authValidator.requireTrainer(auth, "trainer"));
    }

    @Test
    void requireTrainer_nullAuth_throws() {
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainer(null, "trainer"));
    }

    @Test
    void requireTrainer_nullUsername_throws() {
        var auth = LoginRequestDTO.builder().username(null).password("pwd").build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainer(auth, "trainer"));
    }

    @Test
    void requireTrainer_nullPassword_throws() {
        var auth = LoginRequestDTO.builder().username("trainer").password(null).build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainer(auth, "trainer"));
    }

    @Test
    void requireTrainer_mismatchedUsername_throws() {
        var auth = LoginRequestDTO.builder().username("trainer").password("pwd").build();
        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainer(auth, "other"));
    }

    @Test
    void requireTrainer_wrongPassword_throws() {
        var auth = LoginRequestDTO.builder().username("trainer").password("bad").build();
        when(trainerDAO.matchTrainer("trainer", "bad")).thenReturn(false);

        assertThrows(AuthenticationException.class, () -> authValidator.requireTrainer(auth, "trainer"));
    }
}
