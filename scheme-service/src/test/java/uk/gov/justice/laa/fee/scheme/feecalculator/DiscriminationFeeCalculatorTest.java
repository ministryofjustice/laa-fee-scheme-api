package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.DISCRIMINATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class DiscriminationFeeCalculatorTest {

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 300.50, 50.0, 528.24, 0, 450.0",  // Under escape threshold (No VAT)
      "true, 99.50, 300.50, 50.0, 618.24, 90.0, 450.0",  // Under escape threshold limit (VAT applied)
      "false, 99.50, 500.50, 100.0, 778.24, 0, 700.0", // Equal to escape threshold limit (No VAT)
      "true, 99.50, 500.50, 100.0, 918.24, 140, 700.0",  // Equal to escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                        double costOfCounsel, double travelAndWaitingCosts,
                                                        double expectedTotal, double expectedCalculatedVat,
                                                        double expectedHourlyTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = DiscriminationFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertFeeCalculation(result, expectedTotal, expectedCalculatedVat, expectedHourlyTotal, vatIndicator,
        netProfitCosts, costOfCounsel, travelAndWaitingCosts);

    assertThat(result.getWarnings()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 500.50, 201.0, 778.24, 0, 700.0", // Over escape threshold limit (No VAT)
      "true, 99.50, 500.50, 201.0, 918.24, 140.0, 700.0",  // Over escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netProfitCosts,
                                                                   double costOfCounsel, double travelAndWaitingCosts,
                                                                   double expectedTotal, double expectedCalculatedVat,
                                                                   double expectedHourlyTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = DiscriminationFeeCalculator.getFee(feeEntity, feeCalculationRequest);

    assertFeeCalculation(result, expectedTotal, expectedCalculatedVat, expectedHourlyTotal,
        vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);

    assertThat(result.getWarnings()).isNotNull();
    assertThat(result.getWarnings().getFirst()).isEqualTo("123");
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netProfitCosts,
                                             double costOfCounsel, double travelAndWaitingCosts) {
    return FeeCalculationRequest.builder()
        .feeCode("DISC")
        .claimId("claim_123")
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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("DISC_FS2013").build())
        .calculationType(DISCRIMINATION)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, double calculatedVat,
                                    double expectedHourlyTotal, boolean vatIndicator,
                                    double netProfitCosts, double costOfCounsel, double travelAndWaitingCosts) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("DISC");
    assertThat(response.getSchemeId()).isEqualTo("DISC_FS2013");
    assertThat(response.getClaimId()).isEqualTo("claim_123");

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(20);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(calculatedVat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(65.20);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(13.04);
    assertThat(feeCalculation.getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
    assertThat(feeCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(feeCalculation.getNetCostOfCounselAmount()).isEqualTo(costOfCounsel);
    assertThat(feeCalculation.getTravelAndWaitingCostAmount()).isEqualTo(travelAndWaitingCosts);
  }
}