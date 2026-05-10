package org.example.util;

import org.example.dto.request.BaseRequestDTO;
import org.example.model.base.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileUpdaterTest {

    @Mock
    private CredentialGenerator generator;

    @InjectMocks
    private UserProfileUpdater updater;

    private User user;
    private BaseRequestDTO request;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .userName("Ilyas.Azizzade")
                .password("oldPassword")
                .build();

        request = BaseRequestDTO.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .wantsPasswordChange(false)
                .build();
    }

    @Test
    void shouldInjectGeneratorAndWork() {

        CredentialGenerator mockGenerator = mock(CredentialGenerator.class);

        UserProfileUpdater updater = new UserProfileUpdater();
        updater.setCredentialGenerator(mockGenerator);

        User user = User.builder()
                .firstName("Ilyas")
                .lastName("Azizzade")
                .userName("Ilyas.Azizzade")
                .build();

        BaseRequestDTO request = BaseRequestDTO.builder()
                .firstName("Faiq")
                .lastName("Azizzade")
                .build();

        when(mockGenerator.generateUsername("Faiq", "Azizzade"))
                .thenReturn("Faiq.Azizzade");

        updater.updateUserProfile(request, user);

        verify(mockGenerator).generateUsername("Faiq", "Azizzade");
    }

    @Test
    void shouldUpdatePasswordWhenRequested() {
        request.setWantsPasswordChange(true);

        when(generator.generatePassword())
                .thenReturn("newPassword");

        updater.updateUserProfile(request, user);

        assertEquals("newPassword", user.getPassword());

        verify(generator).generatePassword();
    }

    @Test
    void shouldUpdateUsernameAndNamesWhenNameChanged() {

        request.setFirstName("Faiq");
        request.setLastName("Azizzade");

        when(generator.generateUsername("Faiq", "Azizzade"))
                .thenReturn("Faiq.Azizzade");

        updater.updateUserProfile(request, user);

        assertEquals("Faiq", user.getFirstName());
        assertEquals("Azizzade", user.getLastName());
        assertEquals("Faiq.Azizzade", user.getUserName());

        verify(generator)
                .generateUsername("Faiq", "Azizzade");

        verify(generator)
                .removeUsername("Ilyas.Azizzade");
    }

    @Test
    void shouldNotUpdateUsernameWhenNameNotChanged() {
        updater.updateUserProfile(request, user);

        verify(generator, never())
                .generateUsername(any(), any());

        verify(generator, never())
                .removeUsername(any());
    }

    @Test
    void shouldUpdateBothPasswordAndUsername() {

        request.setFirstName("Faiq");
        request.setLastName("Azizzade");
        request.setWantsPasswordChange(true);

        when(generator.generatePassword())
                .thenReturn("newPassword");

        when(generator.generateUsername("Faiq", "Azizzade"))
                .thenReturn("Faiq.Azizzade");

        updater.updateUserProfile(request, user);

        assertEquals("newPassword", user.getPassword());

        assertEquals("Faiq.Azizzade", user.getUserName());

        verify(generator).generatePassword();

        verify(generator)
                .generateUsername("Faiq", "Azizzade");

        verify(generator)
                .removeUsername("Ilyas.Azizzade");
    }
}