package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.DISCRIMINATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class DiscriminationFeeCalculatorTest {

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 300.50, 50.0, 515.2, 528.24",  // Under escape threshold (No VAT)
      "true, 99.50, 300.50, 50.0, 515.2, 618.24" ,  // Under escape threshold limit (VAT applied)
      "false, 99.50, 500.50, 100.0, 765.2, 778.24", // Equal to escape threshold limit (No VAT)
      "true, 99.50, 500.50, 100.0, 765.2, 918.24",  // Equal to escape threshold limit (VAT applied)
      "false, 99.50, 500.50, 201.0, 765.2, 778.24", // Over escape threshold limit (No VAT)
      "true, 99.50, 500.50, 201.0, 765.2, 918.24",  // Over escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                        double costOfCounsel, double travelAndWaitingCosts,
                                                        double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("DISC")
        .startDate(LocalDate.of(2025, 5, 12))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(costOfCounsel)
        .travelAndWaitingCosts(travelAndWaitingCosts)
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(65.20)
        .disbursementVatAmount(13.04)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("DISC")
        .calculationType(DISCRIMINATION)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();

    FeeCalculationResponse result = DiscriminationFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("DISC");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }
}