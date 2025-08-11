package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.entity.CategoryOfLawLookUpEntity;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;

@DataJpaTest
@Testcontainers
public class CategoryOfLawLookUpRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private CategoryOfLawLookUpRepository repository;

  @Test
  void getCategoryOfLawLookupRepositoryById() {
    String feeCode = "CAPA";
    Optional<CategoryOfLawLookUpEntity> result = repository.findByFeeCode(feeCode);

    assertThat(result).isPresent();

    CategoryOfLawLookUpEntity categoryOfLawLookUpEntity = result.get();
    assertThat(categoryOfLawLookUpEntity.getCategoryCode()).isEqualTo("AAP");
    assertThat(categoryOfLawLookUpEntity.getFullDescription()).isEqualTo("Claims Against Public Authorities");
    assertThat(categoryOfLawLookUpEntity.getAreaOfLaw()).isEqualTo("Legal Help");
    assertThat(categoryOfLawLookUpEntity.getFeeCode()).isEqualTo(feeCode);
  }
}
