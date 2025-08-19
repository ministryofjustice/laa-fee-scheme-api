package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeCalculationUtilityTest {

  @CsvSource(value = {
      "null, 89.07, 94.96",
      "false, 89.07, 94.96",
      "true, 89.07, 106.88"
  }, nullValues = {"null"})
  @ParameterizedTest
  void shouldBuildResponse(Boolean vatIndicator, double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.buildFixedFeeResponse(new BigDecimal("59.62"), feeCalculationRequest);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(calculation.getTotalAmount()).isEqualTo(expectedTotal);
  }

}