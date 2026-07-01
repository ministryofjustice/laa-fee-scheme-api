package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ASSOCIATED_CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_DISBURSEMENT_VAT_CAPPED;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
class AssociatedCivilFixedFeeCalculatorTest {

  @Mock
  VatRatesService vatRatesService;

  @InjectMocks
  AssociatedCivilFixedFeeCalculator associatedCivilFixedFeeCalculator;

  private void mockVatRatesService(Boolean vatIndicator) {
    BigDecimal vatRate = vatIndicator ? new BigDecimal("20.00") : BigDecimal.ZERO;
    lenient().when(vatRatesService.getVatRateForDate(any(), any())).thenReturn(vatRate);
    lenient().when(vatRatesService.getVatRateForRequest(any())).thenReturn(vatRate);
    lenient().when(vatRatesService.getVatRate(any(), any())).thenReturn(new BigDecimal("20.00"));
    // Disbursement VAT is always fetched with Boolean.TRUE regardless of vatIndicator
    lenient().when(vatRatesService.getVatRateForDate(any(), eq(Boolean.TRUE))).thenReturn(new BigDecimal("20.00"));
  }

  @ParameterizedTest
  @CsvSource({
      "false, 10.00, 20.00, 170.13, 0",  // Under escape threshold (No VAT)
      "true, 10.00, 20.00, 180.13, 10.00",  // Under escape threshold limit (VAT applied)
      "false, 80.00, 20.00, 170.13, 0", // Equal to escape threshold limit (No VAT)
      "true, 80.00, 20.00, 180.13, 10.00" // Equal to escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponse(boolean vatIndicator, double netTravelCosts,
                                                    double netWaitingCosts, double expectedTotal,
                                                    double expectedVat) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netTravelCosts, netWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, false);
  }


  @ParameterizedTest
  @CsvSource({
      "false, 90.00, 20.00, 170.13, 0", // Over escape threshold limit (No VAT)
      "true, 90.00, 20.00, 180.13, 10.00" // Over escape threshold limit (VAT applied)
  })
  void calculate_shouldReturnFeeCalculationResponseWithWarning(boolean vatIndicator, double netTravelCosts,
                                                               double netWaitingCosts, double expectedTotal,
                                                               double expectedVat) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netTravelCosts, netWaitingCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, true);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message(WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD.getMessage())
        .code(WARN_ASSOCIATED_CIVIL_ESCAPE_THRESHOLD.getCode())
        .type(WARNING)
        .build();

    assertThat(result.getValidationMessages()).containsExactly(validationMessage);
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
        .disbursementVatAmount(20.02)
        .caseConcludedDate(LocalDate.of(2016, 12, 12))
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
    assertThat(feeCalculation.getDisbursementVatAmount()).isEqualTo(20.02);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(50);
  }

  @Test
  void calculate_whenDisbursementVatExceedsCap_shouldReturnWarnDisbursementVatCapped() {

    mockVatRatesService(true);

    // disbursementVatAmount 30.00 > 20% of netDisbursementAmount 100.11 (max = 20.02)
    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("ASMS")
        .claimId("claim_123")
        .uniqueFileNumber("020416/001")
        .vatIndicator(true)
        .netProfitCosts(400.00)
        .netTravelCosts(10.00)
        .netWaitingCosts(20.00)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(30.00)
        .caseConcludedDate(LocalDate.of(2016, 12, 12))
        .build();

    FeeCalculationResponse result = associatedCivilFixedFeeCalculator.calculate(request, buildFeeEntity());

    // fixedFee=50 + vatOnFixed=10 + netDisbAmt=100.11 + cappedDisbVat=20.02 = 180.13
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(180.13);
    assertThat(result.getFeeCalculation().getDisbursementVatAmount()).isEqualTo(20.02);
    assertThat(result.getFeeCalculation().getRequestedDisbursementVatAmount()).isEqualTo(30.00);
    assertThat(result.getValidationMessages()).containsExactly(
        ValidationMessagesInner.builder()
            .code(WARN_DISBURSEMENT_VAT_CAPPED.getCode())
            .message(WARN_DISBURSEMENT_VAT_CAPPED.getMessage())
            .type(WARNING)
            .build()
    );
  }

  @Test
  void getSupportedCategories_shouldReturnAssociatedCivilOnly() {

    Set<CategoryType> result = associatedCivilFixedFeeCalculator.getSupportedCategories();

    assertThat(result).containsExactly(ASSOCIATED_CIVIL);
  }
}