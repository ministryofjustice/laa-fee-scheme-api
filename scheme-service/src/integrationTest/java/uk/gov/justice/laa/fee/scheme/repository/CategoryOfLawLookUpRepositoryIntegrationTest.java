package uk.gov.justice.laa.fee.scheme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.repository.postgresTestContainer.PostgresContainerTestBase;

@DataJpaTest
@Testcontainers
public class CategoryOfLawLookUpRepositoryIntegrationTest extends PostgresContainerTestBase {

  private static final Long CATEGORY_OF_LAW_LOOK_UP_ID = 456L;

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  // will be refactored one we have test data in dev and
  // LFSP-57 populate mediation fee data is merged

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    // to be implemented once flyway data implemented
  }
}
