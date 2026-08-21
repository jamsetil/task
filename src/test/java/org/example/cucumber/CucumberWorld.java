package org.example.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class CucumberWorld {

    private String traineeUsername;
    private String traineePassword;
    private String trainerUsername;
    private String jwtToken;
    private MvcResult lastMvcResult;
    private String lastResponseBody;

    public String getTraineeUsername() {
        return traineeUsername;
    }

    public void setTraineeUsername(String traineeUsername) {
        this.traineeUsername = traineeUsername;
    }

    public String getTraineePassword() {
        return traineePassword;
    }

    public void setTraineePassword(String traineePassword) {
        this.traineePassword = traineePassword;
    }

    public String getTrainerUsername() {
        return trainerUsername;
    }

    public void setTrainerUsername(String trainerUsername) {
        this.trainerUsername = trainerUsername;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public MvcResult getLastMvcResult() {
        return lastMvcResult;
    }

    public void setLastMvcResult(MvcResult lastMvcResult) {
        this.lastMvcResult = lastMvcResult;
        try {
            this.lastResponseBody = lastMvcResult.getResponse().getContentAsString();
        } catch (Exception exception) {
            this.lastResponseBody = "";
        }
    }

    public String getLastResponseBody() {
        return lastResponseBody == null ? "" : lastResponseBody;
    }
}
