package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLAIMS_PUBLIC_AUTHORITIES;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.CLINICAL_NEGLIGENCE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.DEBT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.HOUSING_HLPAS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MISCELLANEOUS;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.WELFARE_BENEFITS;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class OtherCivilFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

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

    mockVatRatesService(vatIndicator);

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

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = buildRequest(vatIndicator, netProfitCosts);
    FeeEntity feeEntity = buildFeeEntity();

    FeeCalculationResponse result = feeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, expectedTotal, vatIndicator, expectedVat, true);

    List<WarningType> warningTypes = WarningType.getByCategory(feeEntity.getCategoryType());

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .message(warningTypes.getFirst().getMessage())
        .code(warningTypes.getFirst().getCode())
        .type(WARNING)
        .build();

    assertThat(result.getValidationMessages()).containsExactly(validationMessage);
  }

  @Test
  void calculate_givenInvalidCategoryThrowsException() {

    mockVatRatesService(true);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .claimId("claim_123")
        .uniqueFileNumber("04052025/224")
        .startDate(LocalDate.of(2025, 4, 5))
        .vatIndicator(true)
        .netProfitCosts(501.00)
        .netDisbursementAmount(370.00)
        .disbursementVatAmount(74.00)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FEE123")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("FEE123_FS2013").build())
        .fixedFee(new BigDecimal("250.00"))
        .categoryType(CategoryType.ADVOCACY_APPEALS_REVIEWS)
        .escapeThresholdLimit(new BigDecimal("500.00"))
        .build();

    assertThatThrownBy(() -> feeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("No warning codes found for category: ADVOCACY_APPEALS_REVIEWS");
  }

  @Test
  void getSupportedCategories_shouldReturnExpectedCategories() {
    Set<CategoryType> result = feeCalculator.getSupportedCategories();

    assertThat(result).containsExactlyInAnyOrder(CLAIMS_PUBLIC_AUTHORITIES, CLINICAL_NEGLIGENCE, COMMUNITY_CARE, DEBT,
        HOUSING, HOUSING_HLPAS, MISCELLANEOUS, PUBLIC_LAW, WELFARE_BENEFITS);
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
}