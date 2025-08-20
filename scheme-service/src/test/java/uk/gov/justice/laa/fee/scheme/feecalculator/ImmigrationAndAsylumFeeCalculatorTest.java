package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM_FIXED_FEE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class ImmigrationAndAsylumFeeCalculatorTest {

  private static final String WARNING_CODE = "123"; // clarify what code should be
  private static final String WARNING_CODE_DESCRIPTION = "123"; // clarify what description should be

  @ParameterizedTest
  @MethodSource("testDataWithDisbursement")
  void getFee_whenImmigrationAndAsylum_withDisbursement(
      String description,
      String feeCode,
      boolean vatIndicator,
      String disbursementPriorAuthority,
      double netDisbursementAmount,
      double disbursementVatAmount,
      BigDecimal netDisbursementLimit,
      double detentionAndWaitingCosts,
      double jrFormfilling,
      double expectedSubTotal,
      double expectedTotal) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .vatIndicator(vatIndicator)
        .disbursementPriorAuthority(disbursementPriorAuthority)
        .boltOns(BoltOnType.builder()
            .boltOnAdjournedHearing(1)
            .boltOnHomeOfficeInterview(2)
            .boltOnCmrhOral(1)
            .boltOnCrmhTelephone(2)
            .build())
        .detentionAndWaitingCosts(detentionAndWaitingCosts)
        .jrFormFilling(jrFormfilling)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .fixedFee(new BigDecimal("75.50"))
        .calculationType(IMMIGRATION_ASYLUM_FIXED_FEE)
        .disbursementLimit(netDisbursementLimit)
        .build();

    FeeCalculationResponse response = ImmigrationAndAsylumFeeCalculator.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  public static Stream<Arguments> testDataWithDisbursement() {
    return Stream.of(
        arguments("IACA, Has Vat, eligible for disbursement, below limit",
            "IACA", true, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 574.50, 659.60),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", true, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 975.50, 1110.60),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", true, null, 800, 100,  BigDecimal.valueOf(600),
            50, 50, 775.50, 910.60),
        arguments("IACA, No Vat, eligible for disbursement, below limit",
            "IACA", false, null, 399, 50, BigDecimal.valueOf(600),
            50, 50, 574.50, 624.50),
        arguments("IACA, No Vat, eligible for disbursement, above limit, with prior auth",
            "IACA", false, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
            50, 50, 975.50, 1075.50),
        arguments("IACA, No Vat, eligible for disbursement, above limit, without prior auth",
            "IACA", false, null, 800, 100,  BigDecimal.valueOf(600),
            50, 50, 775.50, 875.50)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, String priorAuthority, double netDisbursementAmount,
                                     double disbursementVatAmount, BigDecimal netDisbursementLimit, double detentionAndWaitingCosts,
                                     double jrFormfilling, double subtotal, double total) {
    return Arguments.of(scenario, feeCode, vat, priorAuthority, netDisbursementAmount, disbursementVatAmount, netDisbursementLimit,
        detentionAndWaitingCosts, jrFormfilling, subtotal, total);
  }

  @ParameterizedTest
  @CsvSource({
      "IDAS1, true, 75.5, 90.60",
      "IDAS1, false, 75.5, 75.5",
      "IDAS2, true, 75.5, 90.60",
      "IDAS2, false, 75.5, 75.5"})
  void getFee_whenImmigrationAndAsylum_withoutDisbursement(String feeCode, boolean vatIndicator, double expectedSubTotal,
                                                           double expectedTotal) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(null)
        .disbursementVatAmount(null)
        .vatIndicator(vatIndicator)
        .disbursementPriorAuthority(null)
        .boltOns(BoltOnType.builder().build())
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .fixedFee(new BigDecimal("75.50"))
        .calculationType(IMMIGRATION_ASYLUM_FIXED_FEE)
        .disbursementLimit(null)
        .build();

    FeeCalculationResponse response = ImmigrationAndAsylumFeeCalculator.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    assertThat(response.getWarning().getWarrningCode()).isEqualTo(WARNING_CODE);
    assertThat(response.getWarning().getWarningDescription()).isEqualTo(WARNING_CODE_DESCRIPTION);
  }
}
