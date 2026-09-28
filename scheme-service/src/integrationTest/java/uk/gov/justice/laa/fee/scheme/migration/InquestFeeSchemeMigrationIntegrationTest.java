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
import org.junit.jupiter.api.Test;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresSingletonContainer;

class InquestFeeSchemeMigrationIntegrationTest {

  private static final String INQUEST_SCHEME_CODE = "INQUEST_FS2026";
  private static final String PRODUCTION_VALID_FROM = "2026-12-09";
  private static final String LOWER_ENVIRONMENT_VALID_FROM = "2026-09-22";

  private final PostgresSingletonContainer postgres = PostgresSingletonContainer.getInstance();
  private final String schema = "inquest_date_" + UUID.randomUUID().toString().replace("-", "");

  @AfterEach
  void dropSchema() throws SQLException {
    try (Connection connection = getConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("DROP SCHEMA IF EXISTS %s CASCADE".formatted(schema));
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
        .dataSource(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())
        .schemas(schema)
        .defaultSchema(schema)
        .locations("classpath:db/migration", "classpath:db/repeatable")
        .placeholders(Map.of("inquest_valid_from", validFrom))
        .load()
        .migrate();
  }

  private void assertInquestScheme(String expectedValidFrom) throws SQLException {
    String query = """
        SELECT valid_from
        FROM %s.fee_schemes
        WHERE scheme_code = ?
        """.formatted(schema);

    try (Connection connection = getConnection();
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

  private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
  }
}
