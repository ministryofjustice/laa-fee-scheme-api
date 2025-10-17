package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.EscapeCaseCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class OtherCivilFixedFeeCalculatorTest {

  @InjectMocks
  OtherCivilFixedFeeCalculator feeCalculator;

  @ParameterizedTest
  @CsvSource({
      "false, 200.00, 370.33, 0",  // Under escape threshold (No VAT)
      "true, 200.00, 420.33, 50",  // Under escape threshold limit (VAT applied)
      "false, 500.00, 370.33, 0", // Equal to escape threshold limit (No VAT)
      "true, 500.00, 420.33, 50" // Equal to escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netProfitCosts,
                                                    double expectedTotal, double expectedVat) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = feeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, false);
  }

  @ParameterizedTest
  @CsvSource({
      "false, 501.00, 370.33, 0", // Over escape threshold limit (No VAT)
      "true, 501.00, 420.33, 50" // Over escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netProfitCosts,
                                                               double expectedTotal, double expectedVat) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = feeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, true);

    assertEscapeCaseCalculation(result, netProfitCosts);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message("123")
        .type(WARNING)
        .build();

    assertThat(result.getValidationMessages()).size().isEqualTo(1);
    assertThat(result.getValidationMessages().getFirst()).isEqualTo(validationMessage);
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netProfitCosts) {
    return FeeCalculationRequest.builder()
        .feeCode("CAPA")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 4, 5))
        .vatIndicator(vatIndicator)
        .netProfitCosts(netProfitCosts)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.22)
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("CAPA")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("CAPA_FS2013").build())
        .fixedFee(new BigDecimal("250.00"))
        .categoryType(CategoryType.CLAIMS_PUBLIC_AUTHORITIES)
        .escapeThresholdLimit(new BigDecimal("500.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator, double vat,
                                    boolean escapeFlag) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("CAPA");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("CAPA_FS2013");
    assertThat(response.getEscapeCaseFlag()).isEqualTo(escapeFlag);

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(vat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.22);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(250);
  }

  private void assertEscapeCaseCalculation(FeeCalculationResponse response, double netProfitCosts) {
    EscapeCaseCalculation escapeCaseCalculation = response.getEscapeCaseCalculation();
    assertThat(escapeCaseCalculation).isNotNull();
    assertThat(escapeCaseCalculation.getCalculatedEscapeCaseValue()).isEqualTo(netProfitCosts);
    assertThat(escapeCaseCalculation.getEscapeCaseThreshold()).isEqualTo(500.0);
    assertThat(escapeCaseCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
    assertThat(escapeCaseCalculation.getNetProfitCostsAmount()).isEqualTo(netProfitCosts);
  }

}