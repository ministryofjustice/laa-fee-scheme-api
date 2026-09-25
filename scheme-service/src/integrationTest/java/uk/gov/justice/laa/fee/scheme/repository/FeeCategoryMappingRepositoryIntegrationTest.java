package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeCategoryMappingEntity;
import uk.gov.justice.laa.fee.scheme.enums.AreaOfLawType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
class FeeCategoryMappingRepositoryIntegrationTest extends PostgresContainerTestBase {

  private final FeeCategoryMappingRepository repository;

  @Autowired
  public FeeCategoryMappingRepositoryIntegrationTest(FeeCategoryMappingRepository repository) {
    this.repository = repository;
  }

  @Test
  void shouldReturnFeeCategoryMappingEntityWhenFeeCodeIsPresent() {
    String feeCode = "IACA";
    Optional<FeeCategoryMappingEntity> result = repository.findByFeeCodeFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeCategoryMappingEntity feeCategoryMappingEntity = result.get();
    assertThat(feeCategoryMappingEntity.getCategoryOfLawType().getCode()).isEqualTo("IMMAS");
    assertThat(feeCategoryMappingEntity.getFeeCode().getFeeDescription())
        .isEqualTo("Standard Fee - Asylum CLR  (2a)");
    assertThat(feeCategoryMappingEntity.getFeeCode().getFeeType()).isEqualTo(FeeType.FIXED);
  }

  @ParameterizedTest
  @CsvSource(delimiter = '|', value = {
      "MHL11 | Mental Health Tribunal Fee - Levels 1 and 2 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL12 | Mental Health Tribunal Fee - Levels 1, 2 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL13 | Mental Health Tribunal Fee - Level 2 only (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL14 | Mental Health Tribunal Fee - Levels 2 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL15 | Mental Health Tribunal Fee - Level 3 only (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL16 | Mental Health Tribunal Fee - Levels 1 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)"
  })
  void shouldReturnNewMentalHealthCategoryMapping(String feeCode, String description) {
    Optional<FeeCategoryMappingEntity> result = repository.findByFeeCodeFeeCode(feeCode);

    assertThat(result).isPresent();

    FeeCategoryMappingEntity entity = result.get();
    assertThat(entity.getCategoryOfLawType().getCode()).isEqualTo("MHE");
    assertThat(entity.getCategoryOfLawType().getAreaOfLawType().getCode())
        .isEqualTo(AreaOfLawType.LEGAL_HELP);
    assertThat(entity.getFeeCode().getFeeDescription()).isEqualTo(description);
    assertThat(entity.getFeeCode().getFeeType()).isEqualTo(FeeType.FIXED);
  }

  @Test
  void shouldReturnEmptyWhenFeeCodeIsNotPresent() {
    String feeCode = "XYZ";
    Optional<FeeCategoryMappingEntity> result = repository.findByFeeCodeFeeCode(feeCode);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldReturnFeeCategoryMappingsWhenAreaOfLawExists() {

    List<FeeCategoryMappingEntity> result =
        repository.findByCategoryOfLawTypeAreaOfLawTypeCode(AreaOfLawType.LEGAL_HELP);

    assertThat(result).isNotEmpty();

    FeeCategoryMappingEntity entity = result.getFirst();

    assertThat(entity.getCategoryOfLawType()).isNotNull();
    assertThat(entity.getCategoryOfLawType().getAreaOfLawType()).isNotNull();

    assertThat(entity.getCategoryOfLawType().getAreaOfLawType().getCode())
        .isEqualTo(AreaOfLawType.LEGAL_HELP);

    assertThat(entity.getFeeCode()).isNotNull();

    assertThat(entity.getFeeCode().getFeeDescription()).isNotBlank();

    assertThat(entity.getFeeCode().getFeeType()).isIn(FeeType.FIXED, FeeType.HOURLY);
  }
}
