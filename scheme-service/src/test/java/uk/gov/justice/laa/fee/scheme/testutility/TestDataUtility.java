package uk.gov.justice.laa.fee.scheme.testutility;

import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.CLAIMS_PUBLIC_AUTHORITIES;

import java.math.BigDecimal;
import java.time.LocalDate;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType;

public final class TestDataUtility {
  private TestDataUtility() {
  }

  public static FeeSchemesEntity buildFeeSchemesEntity(String schemeCode, String schemeName, LocalDate validFrom) {
    return FeeSchemesEntity.builder()
        .schemeCode(schemeCode)
        .schemeName(schemeName)
        .validFrom(validFrom)
        .build();
  }

  public static FeeEntity buildFeeEntity(String feeCode, BigDecimal fixedFee, CalculationType calculationType) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .fixedFee(fixedFee)
        .calculationType(calculationType)
        .build();
  }
}
