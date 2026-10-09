package uk.gov.justice.laa.fee.scheme.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresSingletonContainer;

class InquestFeeSchemeMigrationIntegrationTest {

  private static final String INQUEST_SCHEME_CODE = "INQUEST_FS2026";
  private static final String PRODUCTION_VALID_FROM = "2026-12-09";
  private static final String LOWER_ENVIRONMENT_VALID_FROM = "2026-09-22";

  private final PostgresSingletonContainer postgres = PostgresSingletonContainer.getInstance();
  private final String database = "inquest_date_" + UUID.randomUUID().toString().replace("-", "");

  @BeforeEach
  void createDatabase() throws SQLException {
    try (Connection connection = getAdminConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE DATABASE %s".formatted(database));
    }
  }

  @AfterEach
  void dropDatabase() throws SQLException {
    try (Connection connection = getAdminConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("DROP DATABASE IF EXISTS %s WITH (FORCE)".formatted(database));
    }
  }

  @Test
  void shouldUpdateInquestValidFromWhenPlaceholderChanges() throws SQLException {
    migrate(PRODUCTION_VALID_FROM);

    assertInquestScheme(PRODUCTION_VALID_FROM);

    migrate(LOWER_ENVIRONMENT_VALID_FROM);

    assertInquestScheme(LOWER_ENVIRONMENT_VALID_FROM);
  }

  private void migrate(String validFrom) {
    Flyway.configure()
        .dataSource(getDatabaseUrl(), postgres.getUsername(), postgres.getPassword())
        .locations("classpath:db/migration", "classpath:db/repeatable")
        .placeholders(Map.of("inquest_valid_from", validFrom))
        .load()
        .migrate();
  }

  private void assertInquestScheme(String expectedValidFrom) throws SQLException {
    String query = """
        SELECT valid_from
        FROM fee_schemes
        WHERE scheme_code = ?
        """;

    try (Connection connection = getDatabaseConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, INQUEST_SCHEME_CODE);

      try (ResultSet resultSet = statement.executeQuery()) {
        assertThat(resultSet.next()).isTrue();
        assertThat(resultSet.getObject("valid_from", LocalDate.class))
            .isEqualTo(LocalDate.parse(expectedValidFrom));
        assertThat(resultSet.next()).isFalse();
      }
    }
  }

  private String getDatabaseUrl() {
    String adminUrl = postgres.getJdbcUrl();
    return adminUrl.substring(0, adminUrl.lastIndexOf('/') + 1) + database;
  }

  private Connection getAdminConnection() throws SQLException {
    return DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
  }

  private Connection getDatabaseConnection() throws SQLException {
    return DriverManager.getConnection(getDatabaseUrl(), postgres.getUsername(), postgres.getPassword());
  }
}
