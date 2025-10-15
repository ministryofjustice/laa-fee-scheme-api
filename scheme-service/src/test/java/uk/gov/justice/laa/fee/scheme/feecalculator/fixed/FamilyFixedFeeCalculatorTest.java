package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.FAMILY;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.util.FeeCalculationUtil;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class FamilyFixedFeeCalculatorTest {

  @InjectMocks
  FamilyFixedFeeCalculator familyFixedFeeCalculator;

  @ParameterizedTest
  @CsvSource({
      "false, 170.33", // No VAT
      "true, 180.33" // VAT applied
  })
  void getFee_shouldReturnFeeCalculationResponse(boolean vatIndicator, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("COM")
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(100.11)
        .disbursementVatAmount(20.22)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("COM")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("COM_FS2013").build())
        .fixedFee(new BigDecimal("50.00"))
        .categoryType(COMMUNITY_CARE)
        .build();

    FeeCalculationResponse result = familyFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("COM");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_ShouldReturnAllExpectedCategories() {
    Set<CategoryType> categories = familyFixedFeeCalculator.getSupportedCategories();

    assertThat(categories).isNotNull();
    assertThat(categories).hasSize(1); // make sure the total count matches
    assertThat(categories).containsExactlyInAnyOrder(
        CategoryType.FAMILY);
  }

  @Test
  void should_returnValidationWarningMessageInResponse_when_total_fee_exceeds_escape_threshold_limit_for_family() {
    BigDecimal fixedFee = new BigDecimal("263.00");
    BigDecimal escapeThresoldLimit = new BigDecimal("550.00");

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FPB010")
        .claimId("claim_124")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(1005.89)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FPB010")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("FAM_NON_LON_FS2011").build())
        .fixedFee(fixedFee)
        .escapeThresholdLimit(escapeThresoldLimit)
        .categoryType(FAMILY)
        .build();

    FeeCalculationResponse response = familyFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1350.94)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(29.45)
        .requestedNetDisbursementAmount(29.45)
        .disbursementVatAmount(1005.89)
        .fixedFeeAmount(263.00)
        .calculatedVatAmount(52.60)
        .build();


    List<ValidationMessagesInner> validationMessages = new ArrayList<>();

    validationMessages.add(ValidationMessagesInner.builder()
        .message("123warning")
        .type(WARNING)
        .build());
    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("FPB010")
        .schemeId("FAM_NON_LON_FS2011")
        .claimId("claim_124")
        .validationMessages(validationMessages)
        .escapeCaseFlag(true)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }

}