package org.example.controller;

import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    void login_returnsOk() {
        var response = authController.login("john", "pwd");

        verify(userService).authenticate("john", "pwd");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeLogin_returnsOk() {
        var request = ChangeLoginRequestDTO.builder()
                .username("john")
                .oldPassword("old")
                .newPassword("new")
                .build();

        var response = authController.changeLogin(request);

        verify(userService).changePassword("john", "old", "new");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
