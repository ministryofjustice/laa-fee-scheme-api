package uk.gov.justice.laa.fee.scheme.postgrestestcontainer;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class PostgresContainerTestBase {

  private static final PostgresSingletonContainer POSTGRES =
      PostgresSingletonContainer.getInstance();

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {

    // ✅ Disable embedded DB replacement (Boot 4 way)
    registry.add("spring.test.database.replace", () -> "none");

    // ✅ Datasource from Testcontainers
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);

    // ✅ Flyway
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add(
        "spring.flyway.locations",
        () -> "classpath:db/migration,classpath:db/repeatable"
    );
  }
}

