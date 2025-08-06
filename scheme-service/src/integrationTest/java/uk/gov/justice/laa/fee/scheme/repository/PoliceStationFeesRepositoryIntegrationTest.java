package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;

@DataJpaTest
@Testcontainers
class PoliceStationFeesRepositoryIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
      .withDatabaseName("testpostgresdb")
      .withUsername("username")
      .withPassword("password");

  @DynamicPropertySource
  static void registerProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add("spring.flyway.locations", () -> "classpath:db/migration,classpath:db/repeatable");
  }

  @Autowired
  private PoliceStationFeesRepository repository;

  @ParameterizedTest
  @MethodSource("feeTestPoliceStation")
  void testFeeByCodeMediation(Long policeStationFeesId,
                              String criminalJusticeArea,
                              String policeStationName,
                              String policeStationCode,
                              BigDecimal fixedFee,
                              BigDecimal escapeThreshold) {

    Optional<PoliceStationFeesEntity> result = repository.findById(policeStationFeesId);
    assertThat(result).isPresent();

    PoliceStationFeesEntity entity = result.get();
    assertThat(entity.getCriminalJusticeArea()).isEqualTo(criminalJusticeArea);
    assertThat(entity.getPoliceStationName()).isEqualTo(policeStationName);
    assertThat(entity.getPoliceStationCode()).isEqualTo(policeStationCode);
    assertThat(entity.getFixedFee()).isEqualTo(fixedFee);
    assertThat(entity.getEscapeThreshold()).isEqualTo(escapeThreshold);
  }

  static Stream<Arguments> feeTestPoliceStation() {
    return Stream.of(
        Arguments.of(
            1L,
            "Hartlepool",
            "Hartlepool",
            "1001",
            new BigDecimal("131.40"),
            new BigDecimal("405.40")
        )
    );
  }
}
