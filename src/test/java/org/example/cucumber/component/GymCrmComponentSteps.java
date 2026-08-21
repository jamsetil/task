package org.example.cucumber.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.cucumber.CucumberWorld;
import org.example.dto.request.ChangeLoginRequestDTO;
import org.example.dto.request.LoginRequestDTO;
import org.example.dto.request.TrainerWorkloadRequest;
import org.example.dto.request.TrainingRequestDTO;
import org.example.dto.request.create.TraineeCreateRequestDTO;
import org.example.dto.request.create.TrainerCreateRequestDTO;
import org.example.enums.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class GymCrmComponentSteps {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private CucumberWorld world;

    @Value("${app.messaging.trainer-workload-queue}")
    private String workloadQueue;

    @Before
    public void drainWorkloadQueueBeforeScenario() {
        drainQueueQuietly();
    }

    @Given("a registered trainee and trainer for cucumber component tests")
    public void aRegisteredTraineeAndTrainer() throws Exception {
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        registerTrainee("CucTrainee" + suffix, "User");
        registerTrainer("CucTrainer" + suffix, "Coach", "Fitness");
    }

    @Given("a registered trainee named {string} {string}")
    public void aRegisteredTraineeNamed(String firstName, String lastName) throws Exception {
        registerTrainee(firstName + UUID.randomUUID().toString().substring(0, 6), lastName);
    }

    @Given("I am authenticated as the registered trainee")
    public void iAmAuthenticatedAsTheRegisteredTrainee() throws Exception {
        loginAndStoreToken(world.getTraineeUsername(), world.getTraineePassword());
    }

    @Given("I deactivate the registered trainee")
    public void iDeactivateTheRegisteredTrainee() throws Exception {
        MvcResult result = mockMvc.perform(patch("/trainees/{username}/status", world.getTraineeUsername())
                        .header("Authorization", "Bearer " + world.getJwtToken())
                        .param("isActive", "false"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Given("I deactivate the registered trainer")
    public void iDeactivateTheRegisteredTrainer() throws Exception {
        MvcResult result = mockMvc.perform(patch("/trainers/{username}/status", world.getTrainerUsername())
                        .header("Authorization", "Bearer " + world.getJwtToken())
                        .param("isActive", "false"))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Given("I drain the workload queue")
    public void iDrainTheWorkloadQueue() {
        drainQueueQuietly();
    }

    @When("I login with the registered trainee credentials")
    public void iLoginWithRegisteredTraineeCredentials() throws Exception {
        performLogin(world.getTraineeUsername(), world.getTraineePassword());
    }

    @When("I login with username of registered trainee and password {string}")
    public void iLoginWithRegisteredUsernameAndPassword(String password) throws Exception {
        performLogin(world.getTraineeUsername(), password);
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithUsernameAndPassword(String username, String password) throws Exception {
        performLogin(username, password);
    }

    @When("I fail login {int} times for the registered trainee")
    public void iFailLoginTimes(int times) throws Exception {
        for (int i = 0; i < times; i++) {
            performLogin(world.getTraineeUsername(), "wrong-password");
            assertThat(world.getLastMvcResult().getResponse().getStatus()).isEqualTo(401);
        }
    }

    @When("I change password from the registered password to {string}")
    public void iChangePasswordFromRegistered(String newPassword) throws Exception {
        performChangePassword(world.getTraineePassword(), newPassword, true);
    }

    @When("I change password from {string} to {string} without authentication")
    public void iChangePasswordWithoutAuth(String oldPassword, String newPassword) throws Exception {
        performChangePassword(oldPassword, newPassword, false);
    }

    @When("I logout")
    public void iLogout() throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer " + world.getJwtToken()))
                .andReturn();
        world.setLastMvcResult(result);
    }

    @When("I register a trainee with firstName {string} and lastName {string}")
    public void iRegisterTrainee(String firstName, String lastName) throws Exception {
        MvcResult result = mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TraineeCreateRequestDTO.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build())))
                .andReturn();
        world.setLastMvcResult(result);
        if (result.getResponse().getStatus() == 200) {
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            world.setTraineeUsername(body.get("userName").asText());
            world.setTraineePassword(body.get("password").asText());
        }
    }

    @When("I register a trainer with firstName {string} lastName {string} specialization {string}")
    public void iRegisterTrainer(String firstName, String lastName, String specialization) throws Exception {
        MvcResult result = mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TrainerCreateRequestDTO.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .specializationName(specialization)
                                .build())))
                .andReturn();
        world.setLastMvcResult(result);
        if (result.getResponse().getStatus() == 200) {
            JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
            world.setTrainerUsername(body.get("userName").asText());
        }
    }

    @When("I get trainee profile for the registered trainee")
    public void iGetTraineeProfile() throws Exception {
        getTraineeProfile(world.getTraineeUsername(), true);
    }

    @When("I get trainee profile for the registered trainee without authentication")
    public void iGetTraineeProfileWithoutAuth() throws Exception {
        getTraineeProfile(world.getTraineeUsername(), false);
    }

    @When("I get trainee profile for username {string}")
    public void iGetTraineeProfileForUsername(String username) throws Exception {
        getTraineeProfile(username, true);
    }

    @When("I get training types")
    public void iGetTrainingTypes() throws Exception {
        MvcResult result = mockMvc.perform(get("/training-types")
                        .header("Authorization", "Bearer " + world.getJwtToken()))
                .andReturn();
        world.setLastMvcResult(result);
    }

    @When("I create a training with duration {int} for the registered users")
    public void iCreateATraining(int duration) throws Exception {
        world.setLastMvcResult(performCreateTraining(
                world.getTraineeUsername(), world.getTrainerUsername(), duration, true));
    }

    @When("I create a training with duration {int} for the registered users without authentication")
    public void iCreateATrainingWithoutAuthentication(int duration) throws Exception {
        world.setLastMvcResult(performCreateTraining(
                world.getTraineeUsername(), world.getTrainerUsername(), duration, false));
    }

    @When("I create a training for trainee with a missing trainer username")
    public void iCreateATrainingForMissingTrainer() throws Exception {
        world.setLastMvcResult(performCreateTraining(
                world.getTraineeUsername(), "missing.trainer." + UUID.randomUUID(), 50, true));
    }

    @When("I create a training for missing trainee with the registered trainer duration {int}")
    public void iCreateATrainingForMissingTrainee(int duration) throws Exception {
        world.setLastMvcResult(performCreateTraining(
                "missing.trainee." + UUID.randomUUID(), world.getTrainerUsername(), duration, true));
    }

    @When("I delete the registered trainee")
    public void iDeleteTheRegisteredTrainee() throws Exception {
        MvcResult result = mockMvc.perform(delete("/trainees/{username}", world.getTraineeUsername())
                        .header("Authorization", "Bearer " + world.getJwtToken()))
                .andReturn();
        world.setLastMvcResult(result);
    }

    @When("I delete trainee username {string}")
    public void iDeleteTraineeUsername(String username) throws Exception {
        MvcResult result = mockMvc.perform(delete("/trainees/{username}", username)
                        .header("Authorization", "Bearer " + world.getJwtToken()))
                .andReturn();
        world.setLastMvcResult(result);
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int status) {
        assertThat(world.getLastMvcResult().getResponse().getStatus()).isEqualTo(status);
    }

    @Then("the response should contain a JWT token")
    public void theResponseShouldContainJwtToken() throws Exception {
        JsonNode body = objectMapper.readTree(world.getLastResponseBody());
        assertThat(body.get("token").asText()).isNotBlank();
        world.setJwtToken(body.get("token").asText());
    }

    @Then("the response message should contain {string}")
    public void theResponseMessageShouldContain(String text) throws Exception {
        JsonNode body = objectMapper.readTree(world.getLastResponseBody());
        assertThat(body.get("message").asText()).containsIgnoringCase(text);
    }

    @Then("the response should contain username and password")
    public void theResponseShouldContainUsernameAndPassword() throws Exception {
        JsonNode body = objectMapper.readTree(world.getLastResponseBody());
        assertThat(body.get("userName").asText()).isNotBlank();
        assertThat(body.get("password").asText()).isNotBlank();
    }

    @Then("the training types list should not be empty")
    public void theTrainingTypesListShouldNotBeEmpty() throws Exception {
        JsonNode body = objectMapper.readTree(world.getLastResponseBody());
        assertThat(body.isArray()).isTrue();
        assertThat(body.size()).isGreaterThan(0);
    }

    @Then("a workload message should be published with actionType {string} and duration {int}")
    public void aWorkloadMessageShouldBePublished(String actionType, int duration) {
        Object payload = jmsTemplate.receiveAndConvert(workloadQueue);
        assertThat(payload).isInstanceOf(TrainerWorkloadRequest.class);

        TrainerWorkloadRequest request = (TrainerWorkloadRequest) payload;
        assertThat(request.getActionType()).isEqualTo(ActionType.valueOf(actionType));
        assertThat(request.getTrainingDuration()).isEqualTo(duration);
        assertThat(request.getTrainerUsername()).isEqualTo(world.getTrainerUsername());
    }

    @Then("no workload message should be published")
    public void noWorkloadMessageShouldBePublished() {
        jmsTemplate.setReceiveTimeout(1000);
        Object payload = jmsTemplate.receiveAndConvert(workloadQueue);
        jmsTemplate.setReceiveTimeout(5000);
        assertThat(payload).isNull();
    }

    private void registerTrainee(String firstName, String lastName) throws Exception {
        MvcResult result = mockMvc.perform(post("/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TraineeCreateRequestDTO.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .build())))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        world.setTraineeUsername(body.get("userName").asText());
        world.setTraineePassword(body.get("password").asText());
    }

    private void registerTrainer(String firstName, String lastName, String specialization) throws Exception {
        MvcResult result = mockMvc.perform(post("/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TrainerCreateRequestDTO.builder()
                                .firstName(firstName)
                                .lastName(lastName)
                                .specializationName(specialization)
                                .build())))
                .andReturn();
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        world.setTrainerUsername(body.get("userName").asText());
    }

    private void loginAndStoreToken(String username, String password) throws Exception {
        performLogin(username, password);
        assertThat(world.getLastMvcResult().getResponse().getStatus()).isEqualTo(200);
        world.setJwtToken(objectMapper.readTree(world.getLastResponseBody()).get("token").asText());
    }

    private void performLogin(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(LoginRequestDTO.builder()
                                .username(username)
                                .password(password)
                                .build())))
                .andReturn();
        world.setLastMvcResult(result);
    }

    private void performChangePassword(String oldPassword, String newPassword, boolean authenticated)
            throws Exception {
        MockHttpServletRequestBuilder builder = put("/auth/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ChangeLoginRequestDTO.builder()
                        .oldPassword(oldPassword)
                        .newPassword(newPassword)
                        .build()));
        if (authenticated) {
            builder.header("Authorization", "Bearer " + world.getJwtToken());
        }
        world.setLastMvcResult(mockMvc.perform(builder).andReturn());
    }

    private void getTraineeProfile(String username, boolean authenticated) throws Exception {
        MockHttpServletRequestBuilder builder = get("/trainees/{username}", username);
        if (authenticated) {
            builder.header("Authorization", "Bearer " + world.getJwtToken());
        }
        world.setLastMvcResult(mockMvc.perform(builder).andReturn());
    }

    private MvcResult performCreateTraining(
            String traineeUsername,
            String trainerUsername,
            int duration,
            boolean authenticated
    ) throws Exception {
        TrainingRequestDTO request = TrainingRequestDTO.builder()
                .traineeUsername(traineeUsername)
                .trainerUsername(trainerUsername)
                .trainingName("Cucumber Training")
                .trainingDate(LocalDate.now())
                .trainingDuration(duration)
                .build();

        MockHttpServletRequestBuilder builder = post("/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));

        if (authenticated) {
            builder.header("Authorization", "Bearer " + world.getJwtToken());
        }

        return mockMvc.perform(builder).andReturn();
    }

    private void drainQueueQuietly() {
        jmsTemplate.setReceiveTimeout(200);
        while (jmsTemplate.receiveAndConvert(workloadQueue) != null) {
            // drain
        }
        jmsTemplate.setReceiveTimeout(5000);
    }
}
