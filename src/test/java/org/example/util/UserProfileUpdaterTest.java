package org.example.util;

import org.example.dto.request.base.BaseUpdateRequestDTO;
import org.example.model.base.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileUpdaterTest {

    @Mock
    private CredentialGenerator generator;

    private UserProfileUpdater updater;

    @BeforeEach
    void setUp() {
        updater = new UserProfileUpdater();
        updater.setCredentialGenerator(generator);
    }

    @Test
    void updateUserProfile_nameChange_regeneratesUsername() {
        var user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .userName("john.smith")
                .password("old")
                .build();

        var request = BaseUpdateRequestDTO.builder()
                .firstName("Jane")
                .lastName("Smith")
                .build();

        when(generator.generateUsername("Jane", "Smith")).thenReturn("jane.smith");

        updater.updateUserProfile(request, user);

        assertEquals("jane.smith", user.getUserName());
        verify(generator).removeUsername("john.smith");
    }

    @Test
    void updateUserProfile_noNameChange_keepsUsername() {
        var user = User.builder()
                .firstName("John")
                .lastName("Smith")
                .userName("john.smith")
                .password("old")
                .build();

        var request = BaseUpdateRequestDTO.builder()
                .firstName("John")
                .lastName("Smith")
                .build();

        updater.updateUserProfile(request, user);

        assertEquals("john.smith", user.getUserName());
    }
}
