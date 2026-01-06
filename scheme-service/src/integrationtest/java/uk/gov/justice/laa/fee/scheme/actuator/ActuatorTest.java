package uk.gov.justice.laa.fee.scheme.actuator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import uk.gov.justice.laa.fee.scheme.config.FeeSchemeTestConfig;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "management.endpoints.web.exposure.include=health,metrics,prometheus"
    }
)
@Import(FeeSchemeTestConfig.class)
class ActuatorTest extends PostgresContainerTestBase {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private static final String URL = "http://localhost:";

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("management.observability.enabled", () -> "false");
  }

  @Test
  void actuatorHealthEndpointShouldReturnUp() {
    ResponseEntity<String> result = restTemplate.getForEntity(URL + port + "/actuator/health", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("\"status\":\"UP\"");
  }

  @Test
  void actuatorMetricsEndpointShouldReturnMetrics() {
    ResponseEntity<String> result = restTemplate.getForEntity(URL + port + "/actuator/metrics", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("application.ready.time");
  }

  @Test
  void actuatorPrometheusEndPointShouldReturnMetrics() {
    ResponseEntity<String> result = restTemplate.getForEntity(URL + port + "/actuator/prometheus", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("application_ready_time_seconds");
  }

}
