package uk.gov.justice.laa.fee.scheme.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.repository.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
@Testcontainers
public class CategoryOfLawLookUpRepositoryIntegrationTest extends PostgresContainerTestBase {

  private static final Long CATEGORY_OF_LAW_LOOK_UP_ID = 456L;

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    // to be implemented once flyway data implemented
  }
}
