package uk.gov.justice.laa.fee.scheme.actuator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

@AutoConfigureObservability
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "management.endpoints.web.exposure.include=health,metrics,prometheus",
    "management.endpoint.health.show-details=always"
})

class ActuatorHealthTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void healthEndPointShouldReturnUp() {
    ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:" + port + "/actuator/health", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("\"status\":\"UP\"");
  }

  @Test
  void healthEndPointShouldReturnMetrics() {
    ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:" + port + "/actuator/metrics", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("application.ready.time");
  }


  @Test
  void healthEndPointShouldReturnPrometheusMetrics() {
    ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:" + port + "/actuator/prometheus", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("application_ready_time_seconds");
  }

}
