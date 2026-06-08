package org.example.controller;

import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void login_returnsOk() throws Exception {
        mockMvc.perform(get("/auth/login")
                        .param("username", "john")
                        .param("password", "pwd"))
                .andExpect(status().isOk());

        verify(userService).authenticate("john", "pwd");
    }

    @Test
    void changeLogin_returnsOk() throws Exception {
        var request = ChangeLoginRequestDTO.builder()
                .username("john")
                .oldPassword("old")
                .newPassword("new")
                .build();

        mockMvc.perform(put("/auth/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService).changePassword("john", "old", "new");
    }
}
