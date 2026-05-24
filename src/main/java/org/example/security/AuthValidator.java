package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.exception.AuthenticationException;
import org.example.validation.RequestValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;
    private final RequestValidator requestValidator;

    public void requireTrainee(LoginRequestDTO auth, String targetUsername) {
        if (auth == null) {
            throw new AuthenticationException("Trainee authentication failed for user: " + targetUsername);
        }
        try {
            requestValidator.validate(auth);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Trainee authentication failed for user: " + targetUsername);
        }
        if (!auth.getUsername().equals(targetUsername)
                || !traineeDAO.matchTrainee(auth.getUsername(), auth.getPassword())) {
            throw new AuthenticationException("Trainee authentication failed for user: " + targetUsername);
        }
    }

    public void requireTrainer(LoginRequestDTO auth, String targetUsername) {
        if (auth == null) {
            throw new AuthenticationException("Trainer authentication failed for user: " + targetUsername);
        }
        try {
            requestValidator.validate(auth);
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException("Trainer authentication failed for user: " + targetUsername);
        }
        if (!auth.getUsername().equals(targetUsername)
                || !trainerDAO.matchTrainer(auth.getUsername(), auth.getPassword())) {
            throw new AuthenticationException("Trainer authentication failed for user: " + targetUsername);
        }
    }
}
