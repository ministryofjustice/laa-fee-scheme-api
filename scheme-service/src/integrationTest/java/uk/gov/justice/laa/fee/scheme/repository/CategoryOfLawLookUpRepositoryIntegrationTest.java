package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeCategoryProjection;

@DataJpaTest
@Testcontainers
public class CategoryOfLawLookUpRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    String feeCode = "CAPA";
    Optional<FeeCategoryProjection> result = repository.findFeeCategoryInfoByFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeCategoryProjection categoryOfLawLookUpEntity = result.get();
    assertThat(categoryOfLawLookUpEntity.getCategoryCode()).isEqualTo("AAP");
    assertThat(categoryOfLawLookUpEntity.getDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(categoryOfLawLookUpEntity.getFeeType()).isEqualTo("FIXED");
  }
}
