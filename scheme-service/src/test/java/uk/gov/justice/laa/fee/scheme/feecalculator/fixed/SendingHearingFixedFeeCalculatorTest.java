package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.SENDING_HEARING;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard.SendingHearingFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class SendingHearingFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  SendingHearingFixedFeeCalculator calculator;

  @ParameterizedTest
  @CsvSource({
      "false, 270.13, 0",
      "true, 300.13, 30.00"
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double expectedTotal,
                                                    double expectedVat) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROW")
        .claimId("claim_123")
        .representationOrderDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.02)
        .caseConcludedDate(LocalDate.of(2026, 1, 30))
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROW")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("SEND_HEAR_FS2022").build())
        .fixedFee(new BigDecimal("150"))
        .categoryType(SENDING_HEARING)
        .build();

    FeeCalculationResponse result = calculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat);
  }

  private void assertFeeCalculation(FeeCalculationResponse response, double total, boolean vatIndicator, double vat) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("PROW");
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("SEND_HEAR_FS2022");

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(vat);
    assertThat(feeCalculation.getDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getRequestedNetDisbursementAmount()).isEqualTo(100.11);
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.02);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(150);
  }



  @ParameterizedTest
  @CsvSource(value = {
      "false, 500, 170.13", // No VAT
      "true, 500, 180.13", // VAT applied
      "true, null, 180.13" // No escape threshold limit
  }, nullValues = "null")
  void calculate_shouldReturnFeeCalculationResponse_withDisbursementLimitWarning(boolean vatIndicator, String escapeThreshold, double expectedTotal) {
    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROW")
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(22.22)
        .caseConcludedDate(LocalDate.of(2025, 5, 12))
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROW")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("SEND_HEAR_FS2022").build())
        .fixedFee(new BigDecimal("50.00"))
        .escapeThresholdLimit(escapeThreshold != null ? new BigDecimal(escapeThreshold) : null)
        .categoryType(SENDING_HEARING)
        .build();

    FeeCalculationResponse result = calculator.calculate(feeCalculationRequest, feeEntity);

    ValidationMessagesInner validationMessage =
        ValidationMessagesInner.builder()
            .message(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getMessage())
            .code(WarningType.WARN_DISBURSEMENT_VAT_CAPPED.getCode())
            .type(WARNING)
            .build();

    assertThat(result.getValidationMessages()).containsExactly(validationMessage);
    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("PROW");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_shouldReturnSendingHearingOnly() {

    Set<CategoryType> result = calculator.getSupportedCategories();

    assertThat(result).containsExactly(SENDING_HEARING);
  }
}
