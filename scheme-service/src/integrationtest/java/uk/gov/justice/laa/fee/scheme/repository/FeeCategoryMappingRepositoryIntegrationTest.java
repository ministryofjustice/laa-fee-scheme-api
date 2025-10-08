package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
@Testcontainers
public class FeeCategoryMappingRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeCategoryMappingRepository repository;

  @Test
  void getFeeDetailsLookupRepositoryById() {
    String feeCode = "IACA";
    Optional<FeeCategoryMappingEntity> result = repository.findFeeCategoryMappingByFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeCategoryMappingEntity feeCategoryMappingEntity = result.get();
    assertThat(feeCategoryMappingEntity.getCategoryOfLawType().getCode()).isEqualTo("IMMAS");
    assertThat(feeCategoryMappingEntity.getFeeDescription()).isEqualTo("Standard Fee - Asylum CLR  (2a)");
    assertThat(feeCategoryMappingEntity.getFeeType()).isEqualTo(FeeType.FIXED);
  }
}
