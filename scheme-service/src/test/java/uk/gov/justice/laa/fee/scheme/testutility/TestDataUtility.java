package uk.gov.justice.laa.fee.scheme.testutility;

import java.math.BigDecimal;
import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType;

/**
 * Utility for building test data.
 */
public final class TestDataUtility {
  private TestDataUtility() {
  }

  /**
   * Build fee scheme entity.
   */
  public static FeeSchemesEntity buildFeeSchemesEntity(String schemeCode, String schemeName, LocalDate validFrom) {
    return FeeSchemesEntity.builder()
        .schemeCode(schemeCode)
        .schemeName(schemeName)
        .validFrom(validFrom)
        .build();
  }

  /**
   * Build fee entity.
   */
  public static FeeEntity buildFeeEntity(String feeCode, BigDecimal fixedFee, CalculationType calculationType,
                                         String schemeCode) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode(schemeCode).build())
        .fixedFee(fixedFee)
        .calculationType(calculationType)
        .build();
  }
}
