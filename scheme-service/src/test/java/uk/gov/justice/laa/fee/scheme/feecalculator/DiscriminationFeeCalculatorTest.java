package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.DISCRIMINATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class DiscriminationFeeCalculatorTest {

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 300.50, 50.0, 515.2, 528.24",  // Under escape threshold (No VAT)
      "true, 99.50, 300.50, 50.0, 515.2, 618.24",  // Under escape threshold limit (VAT applied)
      "false, 99.50, 500.50, 100.0, 765.2, 778.24", // Equal to escape threshold limit (No VAT)
      "true, 99.50, 500.50, 100.0, 765.2, 918.24",  // Equal to escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                        double costOfCounsel, double travelAndWaitingCosts,
                                                        double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = DiscriminationFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertFeeCalculation(result, expectedSubTotal, expectedTotal);

    assertThat(result.getWarning()).isNull();
  }

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 500.50, 201.0, 765.2, 778.24", // Over escape threshold limit (No VAT)
      "true, 99.50, 500.50, 201.0, 765.2, 918.24",  // Over escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netProfitCosts,
                                                        double costOfCounsel, double travelAndWaitingCosts,
                                                        double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = DiscriminationFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertFeeCalculation(result, expectedSubTotal, expectedTotal);

    assertThat(result.getWarning()).isNotNull();
    assertThat(result.getWarning().getWarrningCode()).isEqualTo("123");
    assertThat(result.getWarning().getWarningDescription()).isEqualTo("123");
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netProfitCosts,
                                                           double costOfCounsel, double travelAndWaitingCosts) {
    return FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 12))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(costOfCounsel)
        .travelAndWaitingCosts(travelAndWaitingCosts)
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(65.20)
        .disbursementVatAmount(13.04)
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("DISC")
        .calculationType(DISCRIMINATION)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double subTotal, double total) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("DISC");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getSubTotal()).isEqualTo(subTotal);
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
  }
}