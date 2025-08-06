package uk.gov.justice.laa.fee.scheme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
class FeeSchemesRepositoryIntegrationTest {

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
  private FeeSchemesRepository repository;

  @Test
  void getFeeSchemesById() {
    Optional<FeeSchemesEntity> result = repository.findById("MED_FS2013");
    assertThat(result).isPresent();

    FeeSchemesEntity fee = result.get();
    assertThat(fee.getSchemeCode()).isEqualTo("MED_FS2013");
    assertThat(fee.getSchemeName()).isEqualTo("mediation fee scheme 2013");
    assertThat(fee.getValidFrom()).isEqualTo(LocalDate.parse("2013-04-01"));
    assertThat(fee.getValidTo()).isNull();
  }
}
