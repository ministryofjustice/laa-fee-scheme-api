package uk.gov.justice.laa.fee.scheme.postgresTestContainer;

import org.testcontainers.containers.PostgreSQLContainer;

public enum PostgresSingletonContainer {

  POSTGRES_CONTAINER_INSTANCE;

  private final PostgreSQLContainer<?> postgresContainer;

  PostgresSingletonContainer() {
    postgresContainer = new PostgreSQLContainer<>("postgres:17")
        .withDatabaseName("test_db")
        .withUsername("username")
        .withPassword("password");
    postgresContainer.start();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (postgresContainer.isRunning()) {
        postgresContainer.stop();
      }
    }));
  }

  public static PostgresSingletonContainer getInstance() {
    return POSTGRES_CONTAINER_INSTANCE;
  }

  public String getJdbcUrl() {
    return postgresContainer.getJdbcUrl();
  }

  public String getUsername() {
    return postgresContainer.getUsername();
  }

  public String getPassword() {
    return postgresContainer.getPassword();
  }
}