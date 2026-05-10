package org.example.util;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.BaseRequestDTO;
import org.example.model.base.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserProfileUpdater {

    private CredentialGenerator generator;

    @Autowired
    public void setCredentialGenerator(CredentialGenerator generator) {
        this.generator = generator;
    }

    public void updateUserProfile(BaseRequestDTO requestDTO, User entity) {

        log.debug("Updating user profile for userName={}", entity.getUserName());

        if (requestDTO.isWantsPasswordChange()) {
            entity.setPassword(generator.generatePassword());
            log.info("Password updated for userName={}", entity.getUserName());
        }

        boolean nameChanged =
                !requestDTO.getFirstName().equals(entity.getFirstName())
                        || !requestDTO.getLastName().equals(entity.getLastName());

        if (nameChanged) {

            String oldUserName = entity.getUserName();

            String newUserName = generator.generateUsername(
                    requestDTO.getFirstName(),
                    requestDTO.getLastName()
            );

            entity.setUserName(newUserName);

            generator.removeUsername(oldUserName);

            entity.setFirstName(requestDTO.getFirstName());
            entity.setLastName(requestDTO.getLastName());

            log.info("User profile updated: oldUserName={} → newUserName={}",
                    oldUserName, newUserName);
        } else {
            log.debug("No name change detected for userName={}", entity.getUserName());
        }
    }
}