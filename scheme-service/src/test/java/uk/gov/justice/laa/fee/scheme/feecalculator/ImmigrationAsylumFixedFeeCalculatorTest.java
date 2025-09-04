package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.feecalculator.type.FeeType.FIXED;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.FeeType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class ImmigrationAsylumFixedFeeCalculatorTest {

  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  public static Stream<Arguments> testDataWithDisbursement() {
    return Stream.of(
        arguments("IACA, Has Vat, eligible for disbursement, below limit",
            "IACA", true, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1274.0, 399, 512,
            137.5, 75.5),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", true, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1725.0, 800, 512,
            137.5, 75.5),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", true, null, 800, 100, BigDecimal.valueOf(600),
            50, 50, 1525.0, 600, 512,
            137.5, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, below limit",
            "IACA", false, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 1136.50, 399, 512,
            0, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", false, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 1587.50, 800, 512,
            0, 75.5),
        arguments("IACA, No Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", false, null, 800, 100, BigDecimal.valueOf(600),
            50, 50, 1387.50, 600, 512,
            0, 75.5)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, String priorAuthority, double netDisbursementAmount,
                                     double disbursementVatAmount, BigDecimal netDisbursementLimit, double detentionAndWaitingCosts,
                                     double jrFormfilling, double total, double expectedDisbursementAmount, double expectedBoltonValue,
                                     double expectedCalculatedVatAmount, double expectedFixedFeeAmount) {
    return Arguments.of(scenario, feeCode, vat, priorAuthority, netDisbursementAmount, disbursementVatAmount, netDisbursementLimit,
        detentionAndWaitingCosts, jrFormfilling, total, expectedDisbursementAmount, expectedBoltonValue,
        expectedCalculatedVatAmount, expectedFixedFeeAmount);
  }

  @ParameterizedTest
  @MethodSource("testDataWithDisbursement")
  void getFee_whenImmigrationAndAsylum_withDisbursement(
      String description,
      String feeCode,
      boolean vatIndicator,
      String immigrationPriorityAuthority,
      double netDisbursementAmount,
      double disbursementVatAmount,
      BigDecimal netDisbursementLimit,
      double detentionAndWaitingCosts,
      double jrFormfilling,
      double expectedTotal,
      double expectedDisbursementAmount,
      double expectedBoltonValue,
      double expectedCalculatedVat,
      double expectedFixedFee) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(immigrationPriorityAuthority)
        .boltOns(BoltOnType.builder()
            .boltOnCmrhOral(2)
            .boltOnCmrhTelephone(2)
            .build())
        .detentionAndWaitingCosts(detentionAndWaitingCosts)
        .jrFormFilling(jrFormfilling)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(netDisbursementLimit)
        .oralCmrhBoltOn(BigDecimal.valueOf(166))
        .telephoneCmrhBoltOn(BigDecimal.valueOf(90))
        .build();

    FeeCalculationResponse response = ImmigrationAsylumFixedFeeCalculator.getFee(feeEntity, feeData);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .detentionAndWaitingCostsAmount(detentionAndWaitingCosts)
        .jrFormFillingAmount(jrFormfilling)
        .boltOnFeeAmount(expectedBoltonValue)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("I&A_FS2023")
        .claimId("claim_123")
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @CsvSource({
      "IDAS1, true, 90.60",
      "IDAS1, false, 75.5",
      "IDAS2, true, 90.60",
      "IDAS2, false, 75.5"
  })
  void getFee_whenImmigrationAndAsylum_withoutDisbursement(String feeCode, boolean vatIndicator,
                                                           double expectedTotal) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(null)
        .disbursementVatAmount(null)
        .vatIndicator(vatIndicator)
        .immigrationPriorAuthorityNumber(null)
        .boltOns(BoltOnType.builder().build())
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(IMMIGRATION_ASYLUM)
        .feeType(FIXED)
        .disbursementLimit(null)
        .build();

    FeeCalculationResponse response = ImmigrationAsylumFixedFeeCalculator.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(response.getWarnings().getFirst()).isEqualTo(WARNING_CODE_DESCRIPTION);
  }
}
