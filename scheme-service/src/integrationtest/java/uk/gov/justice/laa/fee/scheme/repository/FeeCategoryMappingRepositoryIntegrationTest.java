package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@SpringBootTest
@Testcontainers
class FeeCategoryMappingRepositoryIntegrationTest extends PostgresContainerTestBase {

  @Autowired
  private FeeCategoryMappingRepository repository;

  @Test
  void should_Return_FeeCategoryMappingEntity_whenFeeCodeIsPresent() {
    String feeCode = "IACA";
    Optional<FeeCategoryMappingEntity> result = repository.findByFeeCodeFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeCategoryMappingEntity feeCategoryMappingEntity = result.get();
    assertThat(feeCategoryMappingEntity.getCategoryOfLawType().getCode()).isEqualTo("IMMAS");
    assertThat(feeCategoryMappingEntity.getFeeCode().getFeeDescription()).isEqualTo("Standard Fee - Asylum CLR  (2a)");
    assertThat(feeCategoryMappingEntity.getFeeCode().getFeeType()).isEqualTo(FeeType.FIXED);
  }

  @Test
  void should_Return_Empty_whenFeeCodeIsNotPresent() {
    String feeCode = "XYZ";
    Optional<FeeCategoryMappingEntity> result = repository.findByFeeCodeFeeCode(feeCode);
    assertThat(result).isEmpty();
  }
}

