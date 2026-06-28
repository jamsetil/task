package org.example.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_withValidCredentials_returnsJwtToken() throws Exception {
        RegisteredUser user = registerTrainee("Auth", "Success");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest(user.username(), user.password()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_withWrongPassword_returnsUnauthorized() throws Exception {
        RegisteredUser user = registerTrainee("Auth", "WrongPass");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest(user.username(), "incorrect-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_withNonExistentUser_returnsUnauthorized() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                loginRequest("ghost.user", "any-password123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void login_withBlankPassword_returnsBadRequest() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"john.doe","password":" "}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void protectedEndpoint_withoutToken_returnsUnauthorized() throws Exception {
        RegisteredUser user = registerTrainee("Auth", "NoToken");

        mockMvc.perform(get("/trainees/{username}", user.username()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_returnsUnauthorized() throws Exception {
        RegisteredUser user = registerTrainee("Auth", "BadToken");

        mockMvc.perform(get("/trainees/{username}", user.username())
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withValidToken_returnsOk() throws Exception {
        RegisteredUser user = registerTrainee("Auth", "WithToken");

        String token = loginAndExtractToken(user);

        mockMvc.perform(get("/trainees/{username}", user.username())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value(user.username()));
    }

    @Test
    void accountLocked_afterThreeFailedLoginAttempts() throws Exception {
        RegisteredUser user = registerTrainee("Lock", "Account");

        for (int attempt = 0; attempt < 3; attempt++) {
            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(
                                    loginRequest(user.username(), "wrong-password"))))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid username or password"));
        }

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest(user.username(), user.password()))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account is locked. Try again in 5 minutes."));
    }

    private RegisteredUser registerTrainee(String firstName, String lastName) throws Exception {
        var request = TraineeCreateRequestDTO.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();

        MvcResult result = mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        return new RegisteredUser(response.get("userName").asText(), response.get("password").asText());
    }

    private String loginAndExtractToken(RegisteredUser user) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest(user.username(), user.password()))))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private static LoginRequestDTO loginRequest(String username, String password) {
        return LoginRequestDTO.builder()
                .username(username)
                .password(password)
                .build();
    }

    private record RegisteredUser(String username, String password) {
    }
}
