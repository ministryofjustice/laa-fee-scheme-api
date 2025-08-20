package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.IMMIGRATION_ASYLUM;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class ImmigrationAndAsylumFeeCalculatorTest {

  @ParameterizedTest
  @MethodSource("testData")
  void getFee_whenImmigrationAndAsylum(
      String description,
      String feeCode,
      boolean vatIndicator,
      String disbursementPriorAuthority,
      double netDisbursementAmount,
      double disbursementVatAmount,
      BigDecimal netDisbursementLimit,
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
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .fixedFee(new BigDecimal("75.50"))
        .calculationType(IMMIGRATION_ASYLUM)
        .disbursementLimit(netDisbursementLimit)
        .build();

    FeeCalculationResponse response = ImmigrationAndAsylumFeeCalculator.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("IACA, Has Vat, eligible for disbursement, below limit",  "IACA", true,
            null, 399, 50, BigDecimal.valueOf(600),  474.50, 539.60),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",  "IACA", true,
            "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),   875.50, 990.60),
        arguments("IACA, Has Vat, eligible for disbursement, above limit, without prior auth",  "IACA", true,
            null, 800, 100,  BigDecimal.valueOf(600),  675.50, 790.60)
    );
  }

  private static Arguments arguments(String scenario,
                                     String feeCode,
                                     boolean vat,
                                     String priorAuthority,
                                     double netDisbursementAmount,
                                     double disbursementVatAmount,
                                     BigDecimal netDisbursementLimit,
                                     double subtotal,
                                     double total) {
    return Arguments.of(scenario, feeCode, vat, priorAuthority, netDisbursementAmount, disbursementVatAmount, netDisbursementLimit, subtotal, total);
  }
}
