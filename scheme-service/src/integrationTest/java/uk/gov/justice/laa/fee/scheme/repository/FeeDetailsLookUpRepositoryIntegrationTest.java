package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.postgresTestContainer.PostgresContainerTestBase;
import uk.gov.justice.laa.fee.scheme.repository.projection.FeeDetailsProjection;

@DataJpaTest
@Testcontainers
public class FeeDetailsLookUpRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeDetailsLookUpRepository repository;

  @Test
  void getFeeDetailsLookupRepositoryById() {
    String feeCode = "CAPA";
    Optional<FeeDetailsProjection> result = repository.findFeeCategoryInfoByFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeDetailsProjection feeDetailsProjection = result.get();
    assertThat(feeDetailsProjection.getCategoryCode()).isEqualTo("AAP");
    assertThat(feeDetailsProjection.getDescription()).isEqualTo("Claims Against Public Authorities Legal Help Fixed Fee");
    assertThat(feeDetailsProjection.getFeeType()).isEqualTo("FIXED");
  }
}
