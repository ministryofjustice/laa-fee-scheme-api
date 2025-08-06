package uk.gov.justice.laa.fee.scheme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
public class CategoryOfLawLookUpRepositoryIntegrationTest {

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

  private static final Long CATEGORY_OF_LAW_LOOK_UP_ID = 456L;

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    // to be implemented once flyway data implemented
  }
}
