package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.COMMUNITY_CARE;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FixedFeeCalculatorTest {

  @ParameterizedTest
  @CsvSource({
      "false, 150.11, 170.33", // No VAT
      "true, 150.11, 180.33" // VAT applied
  })
  void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("COM")
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.22)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("COM")
        .fixedFee(new BigDecimal("50.00"))
        .calculationType(COMMUNITY_CARE)
        .build();

    FeeCalculationResponse result = FixedFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("COM");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

}