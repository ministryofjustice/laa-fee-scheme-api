package uk.gov.justice.laa.fee.scheme.actuator;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.actuate.observability.AutoConfigureObservability;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@AutoConfigureObservability
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "management.endpoints.web.exposure.include=health,metrics,prometheus",
})
class ActuatorTest extends PostgresContainerTestBase {

  @LocalServerPort
  private int port;

  private final TestRestTemplate restTemplate;

  @Autowired
  public ActuatorTest(TestRestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private static final String URL = "http://localhost:";

  @ParameterizedTest(name = "{0} endpoint should return expected response")
  @MethodSource("actuatorEndpoints")
  void actuatorEndpointsShouldReturnExpectedResponse(
      String endpoint,
      String expectedBodyFragment
  ) {
    ResponseEntity<String> result =
        restTemplate.getForEntity(URL + port + endpoint, String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains(expectedBodyFragment);
  }

  private static Stream<Arguments> actuatorEndpoints() {
    return Stream.of(
        Arguments.of("/actuator/health", "\"status\":\"UP\""),
        Arguments.of("/actuator/metrics", "application.ready.time"),
        Arguments.of("/actuator/prometheus", "application_ready_time_seconds")
    );
  }

}