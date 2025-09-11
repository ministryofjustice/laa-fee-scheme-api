package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;

@ExtendWith(MockitoExtension.class)
class StandardFeeCalculatorTest {

  @InjectMocks
  StandardFeeCalculator standardFeeCalculator;

  @Mock
  DataService dataService;

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

    when(dataService.getFeeEntity(any())).thenReturn(feeEntity);

    FeeCalculationResponse result = standardFeeCalculator.calculate(feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("COM");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void getSupportedCategories_ShouldReturnAllExpectedCategories() {


    Set<CategoryType> categories = standardFeeCalculator.getSupportedCategories();


    Assertions.assertNotNull(categories);
    Assertions.assertEquals(11, categories.size()); // make sure the total count matches

    Assertions.assertTrue(categories.contains(CategoryType.CLAIMS_PUBLIC_AUTHORITIES));
    Assertions.assertTrue(categories.contains(CategoryType.CLINICAL_NEGLIGENCE));
    Assertions.assertTrue(categories.contains(COMMUNITY_CARE));
    Assertions.assertTrue(categories.contains(CategoryType.DEBT));
    Assertions.assertTrue(categories.contains(CategoryType.EDUCATION));
    Assertions.assertTrue(categories.contains(CategoryType.HOUSING));
    Assertions.assertTrue(categories.contains(CategoryType.HOUSING_HLPAS));
    Assertions.assertTrue(categories.contains(CategoryType.MENTAL_HEALTH));
    Assertions.assertTrue(categories.contains(CategoryType.MISCELLANEOUS));
    Assertions.assertTrue(categories.contains(CategoryType.PUBLIC_LAW));
    Assertions.assertTrue(categories.contains(CategoryType.WELFARE_BENEFITS));

  }

}