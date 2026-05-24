package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.dao.TraineeDAO;
import org.example.dao.TrainerDAO;
import org.example.dto.request.LoginRequestDTO;
import org.example.exception.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthValidator {

    private final TraineeDAO traineeDAO;
    private final TrainerDAO trainerDAO;

    public void requireTrainee(LoginRequestDTO auth, String targetUsername) {
        if (auth == null
                || auth.getUsername() == null
                || auth.getPassword() == null
                || !auth.getUsername().equals(targetUsername)
                || !traineeDAO.matchTrainee(auth.getUsername(), auth.getPassword())) {
            throw new AuthenticationException("Trainee authentication failed for user: " + targetUsername);
        }
    }

    public void requireTrainer(LoginRequestDTO auth, String targetUsername) {
        if (auth == null
                || auth.getUsername() == null
                || auth.getPassword() == null
                || !auth.getUsername().equals(targetUsername)
                || !trainerDAO.matchTrainer(auth.getUsername(), auth.getPassword())) {
            throw new AuthenticationException("Trainer authentication failed for user: " + targetUsername);
        }
    }
}
