package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.INQUEST;
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
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard.InquestFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class InquestFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  InquestFixedFeeCalculator feeCalculator;

  @ParameterizedTest
  @CsvSource({
      "false, 200.00, 370.13, 0",  // Under escape threshold (No VAT)
      "true, 200.00, 420.13, 50",  // Under escape threshold limit (VAT applied)
      "false, 500.00, 370.13, 0", // Equal to escape threshold limit (No VAT)
      "true, 500.00, 420.13, 50", // Equal to escape threshold limit (VAT applied)
      "false, 900.00, 370.13, 0", // Above escape threshold limit, escape handling not yet supported (No VAT)
      "true, 900.00, 420.13, 50" // Above escape threshold limit, escape handling not yet supported (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                    double expectedTotal, double expectedVat) {

    mockVatRatesService(vatIndicator);

    if (!vatIndicator) {
      mockVatRatesVatIndicatorTrue();
    }

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = feeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat);
  }


  @ParameterizedTest
  @CsvSource({
      "false, 200.00, 370.13, 0",  // Under escape threshold (No VAT)
      "true, 200.00, 420.13, 50",  // Under escape threshold limit (VAT applied)
      "false, 500.00, 370.13, 0", // Equal to escape threshold limit (No VAT)
      "true, 500.00, 420.13, 50" // Equal to escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponseWithWarningOnDisbursementVAT(boolean vatIndicator, double netProfitCosts,
                                                                                double expectedTotal, double expectedVat) {
    mockVatRatesService(vatIndicator);

    if (!vatIndicator) {
      mockVatRatesVatIndicatorTrue();
    }

    FeeCalculationRequest feeCalculationRequest = buildRequestDisbursementVatOverLimit(vatIndicator, netProfitCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = feeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculationDisbursementVatOverLimit(result, expectedTotal, vatIndicator, expectedVat);

    ValidationMessagesInner validationMessage =
            ValidationMessagesInner.builder()
                    .message(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getMessage())
                    .code(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getCode())
                    .type(WARNING)
                    .build();

    assertThat(result.getValidationMessages()).containsExactly(validationMessage);
  }

  @Test
  void getSupportedCategories_shouldReturnExpectedCategories() {
    Set<CategoryType> result = feeCalculator.getSupportedCategories();

    assertThat(result).containsExactlyInAnyOrder(INQUEST);
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netProfitCosts) {
    return FeeCalculationRequest.builder()
        .feeCode("INQ")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 4, 5))
        .vatIndicator(vatIndicator)
        .netProfitCosts(netProfitCosts)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.02)
        .caseConcludedDate(LocalDate.of(2026, 1, 30))
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("INQ")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("INQUEST_FS2026").build())
        .fixedFee(new BigDecimal("250.00"))
        .categoryType(INQUEST)
        .escapeThresholdLimit(new BigDecimal("500.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator, double vat) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("INQ");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("INQUEST_FS2026");
    // Escape-case handling is not yet implemented for Inquest fee codes (separate ticket),
    // so escapeCaseFlag is always null here.
    assertThat(response.getEscapeCaseFlag()).isNull();

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(vat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.02);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(250);
  }

  private FeeCalculationRequest buildRequestDisbursementVatOverLimit(boolean vatIndicator, double netProfitCosts) {
    return FeeCalculationRequest.builder()
            .feeCode("INQ")
            .claimId("claim_123")
            .startDate(LocalDate.of(2025, 4, 5))
            .caseConcludedDate(LocalDate.of(2025, 5, 12))
            .vatIndicator(vatIndicator)
            .netProfitCosts(netProfitCosts)
            .netDisbursementAmount(100.11)
            .disbursementVatAmount(22.02)
            .build();
  }

  private void assertFeeCalculationDisbursementVatOverLimit(FeeCalculationResponse response, double total,
                                                            boolean vatIndicator, double vat) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("INQ");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("INQUEST_FS2026");
    assertThat(response.getEscapeCaseFlag()).isNull();

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(vat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.02);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(250);
  }
}
