package org.example.monitoring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorEndpointTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void healthEndpointIncludesCustomIndicators() {
        var response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
        assertThat(response.getBody()).contains("trainingTypeCatalog");
        assertThat(response.getBody()).contains("userProfile");
        assertThat(response.getBody()).contains("trainingData");
    }

    @Test
    void prometheusEndpointExposesGymMetrics() {
        var response = restTemplate.getForEntity("/actuator/prometheus", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("gym_trainees");
        assertThat(response.getBody()).contains("gym_trainers");
        assertThat(response.getBody()).contains("gym_trainings");
    }
}
