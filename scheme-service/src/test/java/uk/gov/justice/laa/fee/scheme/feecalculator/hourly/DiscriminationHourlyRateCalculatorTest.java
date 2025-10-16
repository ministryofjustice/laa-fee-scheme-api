package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DISCRIMINATION;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class DiscriminationHourlyRateCalculatorTest {

  @InjectMocks
  DiscriminationHourlyRateCalculator discriminationHourlyRateCalculator;

  @ParameterizedTest
  @CsvSource({
      "false, 149.50, 300.50, 528.24, 0, 450.00",  // Under escape threshold (No VAT)
      "true, 149.50, 300.50, 618.24, 90.00, 450.00",  // Under escape threshold limit (VAT applied)
      "false, 199.50, 500.50, 778.24, 0, 700.00", // Equal to escape threshold limit (No VAT)
      "true, 199.50, 500.50, 918.24, 140.00, 700.00" // Equal to escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts, double costOfCounsel,
                                                    double expectedTotal, double expectedVat, double expectedHourlyTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = discriminationHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, netProfitCosts, costOfCounsel,
        expectedVat, expectedHourlyTotal, false);

    assertThat(result.getValidationMessages()).isEmpty();
  }

  @ParameterizedTest
  @CsvSource({
      "false, 300.50, 500.50, 778.24, 0, 700.00", // Over escape threshold limit (No VAT)
      "true, 300.50, 500.50, 918.24, 140.00, 700.00"  // Over escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netProfitCosts,
                                                               double costOfCounsel, double expectedTotal,
                                                               double expectedVat, double expectedHourlyTotal) {
    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts, costOfCounsel);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = discriminationHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, netProfitCosts, costOfCounsel, expectedVat,
        expectedHourlyTotal, true);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message("123")
        .type(WARNING)
        .build();

    assertThat(result.getValidationMessages()).size().isEqualTo(1);
    assertThat(result.getValidationMessages().getFirst()).isEqualTo(validationMessage);
  }

  @Test
  void getSupportedCategories_shouldReturnDiscriminationCategory() {
    Set<CategoryType> result = discriminationHourlyRateCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(DISCRIMINATION));
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netProfitCosts,
                                             double costOfCounsel) {
    return FeeCalculationRequest.builder()
        .feeCode("DISC")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 5, 12))
        .netProfitCosts(netProfitCosts)
        .netCostOfCounsel(costOfCounsel)
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(65.20)
        .disbursementVatAmount(13.04)
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("DISC")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("DISC_FS2013").build())
        .categoryType(DISCRIMINATION)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator,
                                    double netProfitCosts, double costOfCounsel, double expectedVat,
                                    double expectedHourlyTotal, boolean expectedEscapeFlag) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("DISC");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getEscapeCaseFlag()).isEqualTo(expectedEscapeFlag);

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
    assertThat(calculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(calculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(calculation.getCalculatedVatAmount()).isEqualTo(expectedVat);
    assertThat(calculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(calculation.getRequestedNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(calculation.getNetCostOfCounselAmount()).isEqualTo(costOfCounsel);
    assertThat(calculation.getDisbursementAmount()).isEqualTo(65.20);
    assertThat(calculation.getRequestedNetDisbursementAmount()).isEqualTo(65.20);
    assertThat(calculation.getDisbursementVatAmount()).isEqualTo(13.04);
    assertThat(calculation.getHourlyTotalAmount()).isEqualTo(expectedHourlyTotal);
  }
}