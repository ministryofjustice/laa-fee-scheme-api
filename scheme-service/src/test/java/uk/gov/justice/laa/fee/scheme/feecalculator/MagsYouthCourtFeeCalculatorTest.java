package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PUBLIC_LAW;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MagsYouthCourtDesignatedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MagsYouthCourtUndesignatedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class MagsYouthCourtFeeCalculatorTest {

  @Mock
  private MagsYouthCourtUndesignatedFeeCalculator undesignatedFeeCalculator;

  @Mock
  private MagsYouthCourtDesignatedFeeCalculator designatedFeeCalculator;

  @InjectMocks
  private MagsYouthCourtFeeCalculator magsYouthCourtFeeCalculator;

  @Test
  void shouldReturnAllSupportedCategories() {
    Set<CategoryType> supportedCategories = magsYouthCourtFeeCalculator.getSupportedCategories();

    assertThat(supportedCategories).contains(
        CategoryType.MAGS_COURT_DESIGNATED,
        CategoryType.MAGS_COURT_UNDESIGNATED,
        CategoryType.YOUTH_COURT_DESIGNATED,
        CategoryType.YOUTH_COURT_UNDESIGNATED
    );
  }

  @Test
  void shouldThrowException_whenCategoryTypeIsUnsupported() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().build();
    FeeEntity feeEntity = FeeEntity.builder().categoryType(PUBLIC_LAW).build();

    assertThatThrownBy(() -> magsYouthCourtFeeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Unexpected category: PUBLIC_LAW");
  }

  @ParameterizedTest
  @EnumSource(
      value = CategoryType.class,
      names = { "MAGS_COURT_DESIGNATED", "YOUTH_COURT_DESIGNATED" }
  )
  void shouldUseStandardFeeCalculator_whenDesignatedTypeCategory(CategoryType categoryType) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().build();
    FeeEntity feeEntity = FeeEntity.builder()
        .categoryType(categoryType)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("TEST")
        .build();

    when(designatedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);
    magsYouthCourtFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    verify(designatedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
  }

  @ParameterizedTest
  @EnumSource(
      value = CategoryType.class,
      names = { "MAGS_COURT_UNDESIGNATED", "YOUTH_COURT_UNDESIGNATED" }
  )
  void shouldUseStandardFeeCalculator_whenUndesignatedTypeCategory(CategoryType categoryType) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().build();
    FeeEntity feeEntity = FeeEntity.builder()
        .categoryType(categoryType)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("TEST")
        .build();

    when(undesignatedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);
    magsYouthCourtFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    verify(undesignatedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
  }
}