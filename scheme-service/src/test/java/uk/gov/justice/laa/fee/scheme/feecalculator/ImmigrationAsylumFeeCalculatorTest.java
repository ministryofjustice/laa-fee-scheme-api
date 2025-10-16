package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.ImmigrationAsylumDisbursementOnlyCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.ImmigrationAsylumFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.ImmigrationAsylumHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumFeeCalculatorTest {

  @Mock
  ImmigrationAsylumFixedFeeCalculator immigrationAsylumFixedFeeCalculator;

  @Mock
  ImmigrationAsylumHourlyRateCalculator immigrationAsylumHourlyRateCalculator;

  @Mock
  ImmigrationAsylumDisbursementOnlyCalculator immigrationAsylumDisbursementOnlyCalculator;

  @InjectMocks
  ImmigrationAsylumFeeCalculator immigrationAsylumFeeCalculator;

  @ParameterizedTest
  @CsvSource({
      "IMMIGRATION_ASYLUM, FIXED",
      "IMMIGRATION_ASYLUM, HOURLY",
      "IMMIGRATION_ASYLUM, DISB_ONLY"
  })
  void shouldReturnCorrectCalculator_whenGivenCategoryAndType(CategoryType categoryType, FeeType feeType) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder().build();
    FeeEntity feeEntity = FeeEntity.builder()
        .categoryType(categoryType)
        .feeType(feeType)
        .build();

    immigrationAsylumFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    switch (feeType) {
      case FIXED -> verify(immigrationAsylumFixedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
      case HOURLY -> verify(immigrationAsylumHourlyRateCalculator).calculate(feeCalculationRequest, feeEntity);
      case DISB_ONLY -> verify(immigrationAsylumDisbursementOnlyCalculator).calculate(feeCalculationRequest, feeEntity);
    }
  }

  @Test
  void getSupportedCategories_shouldReturnImmigrationAsylum() {
    Set<CategoryType> result = immigrationAsylumFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(IMMIGRATION_ASYLUM));
  }
}