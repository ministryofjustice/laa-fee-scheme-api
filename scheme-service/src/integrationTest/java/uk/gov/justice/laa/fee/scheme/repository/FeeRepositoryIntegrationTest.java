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
  @CsvSource({
      "YOUE1, 822.47, 313.19, 542.63, 904.72, 344.51, 596.89",
      "YOUF1, 1072.74, 313.19, 542.63, 1180.01, 344.51, 596.89",
      "YOUE2, 182.01, 313.19, 542.63, 200.21, 344.51, 596.89",
      "YOUF2, 437.81, 313.19, 542.63, 481.59, 344.51, 596.89",
      "YOUE3, 919.96, 538.02, 896.59, 1011.96, 591.82, 986.25",
      "YOUF3, 1335.67, 538.02, 896.59, 1469.24, 591.82, 986.25",
      "YOUE4, 321.37, 538.02, 896.59, 353.51, 591.82, 986.25",
      "YOUF4, 737.08, 538.02, 896.59, 810.79, 591.82, 986.25",
      "YOUX1, 822.47, 313.19, 542.63, 904.72, 344.51, 596.89",
      "YOUX3, 1072.74, 313.19, 542.63, 1180.01, 344.51, 596.89",
      "YOUX2, 182.01, 313.19, 542.63, 200.21, 344.51, 596.89",
      "YOUX4, 437.81, 313.19, 542.63, 481.59, 344.51, 596.89",
      "YOUK1, 884.61, 313.19, 542.63, 973.07, 344.51, 596.89",
      "YOUL1, 1141.17, 313.19, 542.63, 1255.29, 344.51, 596.89",
      "YOUK2, 232.53, 313.19, 542.63, 255.78, 344.51, 596.89",
      "YOUL2, 500.99, 313.19, 542.63, 551.09, 344.51, 596.89",
      "YOUK3, 995.73, 538.02, 896.59, 1095.30, 591.82, 986.25",
      "YOUL3, 1430.44, 538.02, 896.59, 1573.48, 591.82, 986.25",
      "YOUK4, 397.14, 538.02, 896.59, 436.85, 591.82, 986.25",
      "YOUL4, 831.85, 538.02, 896.59, 915.04, 591.82, 986.25",
      "YOUY1, 884.61, 313.19, 542.63, 973.07, 344.51, 596.89",
      "YOUY3, 1141.17, 313.19, 542.63, 1255.29, 344.51, 596.89",
      "YOUY2, 232.53, 313.19, 542.63, 255.78, 344.51, 596.89",
      "YOUY4, 500.99, 313.19, 542.63, 551.09, 344.51, 596.89"
  })
  void shouldPopulateYouthCourtLimitsFor2024And2025(String feeCode,
                                                    BigDecimal fixedFee2024,
                                                    BigDecimal lowerLimit2024,
                                                    BigDecimal higherLimit2024,
                                                    BigDecimal fixedFee2025,
                                                    BigDecimal lowerLimit2025,
                                                    BigDecimal higherLimit2025) {
    List<FeeEntity> result = repository.findByFeeCode(feeCode);

    assertThat(result)
        .extracting(entity -> entity.getFeeScheme().getSchemeCode(),
            FeeEntity::getFixedFee,
            FeeEntity::getLowerStandardFeeLimit,
            FeeEntity::getHigherStandardFeeLimit,
            FeeEntity::getTotalLimit)
        .containsExactlyInAnyOrder(
            tuple("YOUTH_COURT_FS2024", fixedFee2024, lowerLimit2024, higherLimit2024, null),
            tuple("YOUTH_COURT_FS2025", fixedFee2025, lowerLimit2025, higherLimit2025, null)
        );
  }

}