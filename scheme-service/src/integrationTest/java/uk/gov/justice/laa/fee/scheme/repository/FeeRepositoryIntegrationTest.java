package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.postgrestestcontainer.PostgresContainerTestBase;

@DataJpaTest
class FeeRepositoryIntegrationTest extends PostgresContainerTestBase {

  private final FeeRepository repository;

  @Autowired
  public FeeRepositoryIntegrationTest(FeeRepository repository) {
    this.repository = repository;
  }

  @Test
  void testFeeByCode() {
    FeeSchemesEntity feeSchemesEntity = new FeeSchemesEntity();
    feeSchemesEntity.setSchemeCode("PUB_FS2013");

    List<FeeEntity> result = repository.findByFeeCode("PUB");
    assertThat(result).hasSize(1);

    FeeEntity entity = result.getFirst();

    assertThat(entity.getFeeCode()).isEqualTo("PUB");
    assertThat(entity.getDescription()).isEqualTo("Public Law Legal Help Fixed Fee");
    assertThat(entity.getFixedFee()).isEqualTo(new BigDecimal("259.00"));
    assertThat(entity.getEscapeThresholdLimit()).isEqualTo(new BigDecimal("777.00"));
    assertThat(entity.getFeeScheme().getSchemeCode()).isEqualTo("PUB_FS2013");
    assertThat(entity.getCategoryType()).isEqualTo(CategoryType.PUBLIC_LAW);
    assertThat(entity.getFeeType()).isEqualTo(FeeType.FIXED);
  }

  @ParameterizedTest
  @CsvSource({
      "PROE1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROF1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROE2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROF2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROE3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROF3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROJ1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ3, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ4, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROV1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROV2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROV3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROV4, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROK1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROL1, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROK2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROL2, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROK3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROL3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROJ5, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ7, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ6, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89",
      "PROJ8, MAGS_COURT_FS2016, 272.34, 471.85, MAGS_COURT_FS2022, 313.19, 542.63, MAGS_COURT_FS2025, 344.51, 596.89"
  })
  void shouldPopulateMagistratesCourtLimitsForAllSchemes(String feeCode,
                                                         String schemeCodeOne,
                                                         BigDecimal lowerLimitOne,
                                                         BigDecimal higherLimitOne,
                                                         String schemeCodeTwo,
                                                         BigDecimal lowerLimitTwo,
                                                         BigDecimal higherLimitTwo,
                                                         String schemeCodeThree,
                                                         BigDecimal lowerLimitThree,
                                                         BigDecimal higherLimitThree) {
    List<FeeEntity> result = repository.findByFeeCode(feeCode);

    assertThat(result)
        .extracting(entity -> entity.getFeeScheme().getSchemeCode(),
            FeeEntity::getLowerStandardFeeLimit,
            FeeEntity::getHigherStandardFeeLimit,
            FeeEntity::getTotalLimit)
        .containsExactlyInAnyOrder(
            tuple(schemeCodeOne, lowerLimitOne, higherLimitOne, null),
            tuple(schemeCodeTwo, lowerLimitTwo, higherLimitTwo, null),
            tuple(schemeCodeThree, lowerLimitThree, higherLimitThree, null)
        );
  }

  @ParameterizedTest
  @CsvSource(delimiter = '|', value = {
      "MHL11 | 450.00 | Mental Health Tribunal Fee - Levels 1 and 2 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL12 | 744.00 | Mental Health Tribunal Fee - Levels 1, 2 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL13 | 321.00 | Mental Health Tribunal Fee - Level 2 only (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL14 | 615.00 | Mental Health Tribunal Fee - Levels 2 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL15 | 294.00 | Mental Health Tribunal Fee - Level 3 only (Rule 11(7)(a) cases where a patient has not engaged with the provider)",
      "MHL16 | 423.00 | Mental Health Tribunal Fee - Levels 1 and 3 (Rule 11(7)(a) cases where a patient has not engaged with the provider)"
  })
  void shouldReturnNewMentalHealthFee(String feeCode, String fixedFee, String description) {
    List<FeeEntity> result = repository.findByFeeCode(feeCode);

    assertThat(result).singleElement().satisfies(entity -> {
      assertThat(entity.getFeeCode()).isEqualTo(feeCode);
      assertThat(entity.getDescription()).isEqualTo(description);
      assertThat(entity.getFixedFee()).isEqualTo(new BigDecimal(fixedFee));
      assertThat(entity.getEscapeThresholdLimit()).isNull();
      assertThat(entity.getAdjornHearingBoltOn()).isEqualTo(new BigDecimal("117.00"));
      assertThat(entity.getFeeScheme().getSchemeCode()).isEqualTo("MHL_FS2024");
      assertThat(entity.getCategoryType()).isEqualTo(CategoryType.MENTAL_HEALTH);
      assertThat(entity.getFeeType()).isEqualTo(FeeType.FIXED);
    });
  }

}