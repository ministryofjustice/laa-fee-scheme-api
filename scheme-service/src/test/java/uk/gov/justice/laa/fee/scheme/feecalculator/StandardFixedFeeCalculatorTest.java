package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;

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
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.StandardFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class StandardFixedFeeCalculatorTest {

  @InjectMocks
  StandardFixedFeeCalculator standardFixedFeeCalculator;

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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("COM_FS2013").build())
        .fixedFee(new BigDecimal("50.00"))
        .categoryType(COMMUNITY_CARE)
        .build();

    FeeCalculationResponse result = standardFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("COM");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_ShouldReturnAllExpectedCategories() {
    Set<CategoryType> categories = standardFixedFeeCalculator.getSupportedCategories();

    assertThat(categories).isNotNull();
    assertThat(categories).hasSize(12); // make sure the total count matches
    assertThat(categories).containsExactlyInAnyOrder(
        CategoryType.ASSOCIATED_CIVIL, CategoryType.CLAIMS_PUBLIC_AUTHORITIES,
        CategoryType.CLINICAL_NEGLIGENCE, CategoryType.COMMUNITY_CARE,
        CategoryType.DEBT, CategoryType.EDUCATION,
        CategoryType.HOUSING, CategoryType.HOUSING_HLPAS,
        CategoryType.MENTAL_HEALTH, CategoryType.MISCELLANEOUS,
        CategoryType.PUBLIC_LAW, CategoryType.WELFARE_BENEFITS);
  }

}