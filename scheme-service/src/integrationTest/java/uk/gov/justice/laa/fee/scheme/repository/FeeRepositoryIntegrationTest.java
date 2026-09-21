package uk.gov.justice.laa.fee.scheme.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;
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
      "PROK3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25",
      "PROL3, MAGS_COURT_FS2016, 467.84, 779.64, MAGS_COURT_FS2022, 538.02, 896.59, MAGS_COURT_FS2025, 591.82, 986.25"
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

}