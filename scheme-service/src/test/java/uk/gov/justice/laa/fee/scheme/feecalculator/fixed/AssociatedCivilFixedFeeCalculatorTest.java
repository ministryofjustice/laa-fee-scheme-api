package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ASSOCIATED_CIVIL;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class AssociatedCivilFixedFeeCalculatorTest {

  @InjectMocks
  AssociatedCivilFixedFeeCalculator associatedCivilFixedFeeCalculator;

  @ParameterizedTest
  @CsvSource({
      "false, 10.00, 20.00, 170.33, 0",  // Under escape threshold (No VAT)
      "true, 10.00, 20.00, 180.33, 10.00",  // Under escape threshold limit (VAT applied)
      "false, 80.00, 20.00, 170.33, 0", // Equal to escape threshold limit (No VAT)
      "true, 80.00, 20.00, 180.33, 10.00" // Equal to escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netTravelCosts,
                                                    double netWaitingCosts, double expectedTotal,
                                                    double expectedVat) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netTravelCosts, netWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, false);
  }

  @ParameterizedTest
  @CsvSource({
      "false, 90.00, 20.00, 170.33, 0", // Over escape threshold limit (No VAT)
      "true, 90.00, 20.00, 180.33, 10.00" // Over escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netTravelCosts,
                                                               double netWaitingCosts, double expectedTotal,
                                                               double expectedVat) {

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netTravelCosts, netWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, true);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message("123")
        .type(WARNING)
        .build();

    assertThat(result.getValidationMessages()).size().isEqualTo(1);
    assertThat(result.getValidationMessages().getFirst()).isEqualTo(validationMessage);
  }

  private FeeCalculationRequest buildRequest(boolean vatIndicator, double netTravelCosts,
                                             double netWaitingCosts) {
    return FeeCalculationRequest.builder()
        .feeCode("ASMS")
        .claimId("claim_123")
        .uniqueFileNumber("020416/001")
        .vatIndicator(vatIndicator)
        .netProfitCosts(400.00)
        .netTravelCosts(netTravelCosts)
        .netWaitingCosts(netWaitingCosts)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.22)
        .build();
  }

  private FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("ASMS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("ASSOC_FS2016").build())
        .fixedFee(new BigDecimal("50.00"))
        .categoryType(ASSOCIATED_CIVIL)
        .escapeThresholdLimit(new BigDecimal("500.00"))
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator, double vat,
                                    boolean escapeFlag) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("ASMS");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("ASSOC_FS2016");
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
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(50);
  }
}