package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.DiscriminationHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.FeeDataService;

@ExtendWith(MockitoExtension.class)
class DiscriminationHourlyRateCalculatorTest {

  @InjectMocks
  DiscriminationHourlyRateCalculator discriminationHourlyRateCalculator;

  @Mock
  FeeDataService feeDataService;

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 300.50, 50.0, 528.24",  // Under escape threshold (No VAT)
      "true, 99.50, 300.50, 50.0, 618.24",  // Under escape threshold limit (VAT applied)
      "false, 99.50, 500.50, 100.0, 778.24", // Equal to escape threshold limit (No VAT)
      "true, 99.50, 500.50, 100.0, 918.24",  // Equal to escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                        double costOfCounsel, double travelAndWaitingCosts,
                                                        double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();
    when(feeDataService.getFeeEntity(any())).thenReturn(feeEntity);

    FeeCalculationResponse result = discriminationHourlyRateCalculator.calculate(feeCalculationRequest);

    assertFeeCalculation(result, expectedTotal, vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);

    assertThat(result.getWarnings()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
      "false, 99.50, 500.50, 201.0, 778.24", // Over escape threshold limit (No VAT)
      "true, 99.50, 500.50, 201.0, 918.24",  // Over escape threshold limit (VAT applied)
  })
  public void getFee_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netProfitCosts,
                                                                   double costOfCounsel, double travelAndWaitingCosts,
                                                                   double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    when(feeDataService.getFeeEntity(any())).thenReturn(feeEntity);

    FeeCalculationResponse result = discriminationHourlyRateCalculator.calculate(feeCalculationRequest);

    assertFeeCalculation(result, expectedTotal, vatIndicator, netProfitCosts, costOfCounsel, travelAndWaitingCosts);

    assertThat(result.getWarnings()).isNotNull();
    assertThat(result.getWarnings().getFirst()).isEqualTo("123");
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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("DISC_FS2013").build())
        .categoryType(DISCRIMINATION)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator,
                                    double netProfitCosts, double costOfCounsel, double travelAndWaitingCosts) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("DISC");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
    assertThat(calculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(calculation.getVatRateApplied()).isEqualTo(20);
    assertThat(calculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(calculation.getRequestedNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(calculation.getNetCostOfCounselAmount()).isEqualTo(costOfCounsel);
    assertThat(calculation.getTravelAndWaitingCostAmount()).isEqualTo(travelAndWaitingCosts);
  }
}