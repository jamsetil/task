package org.example.controller;

import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.dto.request.LoginRequestDTO;
import org.example.filter.JwtAuthFilter;
import org.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void login_returnsToken() throws Exception {
        when(userService.authenticate("john", "pwd")).thenReturn("jwt-token");

        var request = LoginRequestDTO.builder()
                .username("john")
                .password("pwd")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));

        verify(userService).authenticate("john", "pwd");
    }

    @Test
    void logout_returnsOk() throws Exception {
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer jwt-token"))
                .andExpect(status().isOk());

        verify(userService).logout("Bearer jwt-token");
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
