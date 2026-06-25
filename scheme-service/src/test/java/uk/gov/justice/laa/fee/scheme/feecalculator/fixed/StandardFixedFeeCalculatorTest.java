package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.COMMUNITY_CARE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.standard.StandardFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
class StandardFixedFeeCalculatorTest {

  @Mock
  private VatRatesService vatRatesService;

  /**
   * Subclass that does not override handleEscapeCase, so the base implementation's
   * default `return false` is exercised when canEscape is true.
   */
  private static class TestStandardFixedFeeCalculator extends StandardFixedFeeCalculator {
    TestStandardFixedFeeCalculator(VatRatesService vatRatesService) {
      super(vatRatesService, true);
    }

    @Override
    public Set<CategoryType> getSupportedCategories() {
      return Set.of(COMMUNITY_CARE);
    }
  }

  @Test
  void handleEscapeCase_defaultImplementation_returnsFalse() {
    when(vatRatesService.getVatRateForDate(any(), any())).thenReturn(BigDecimal.ZERO);

    TestStandardFixedFeeCalculator calculator = new TestStandardFixedFeeCalculator(vatRatesService);

    FeeCalculationRequest request = FeeCalculationRequest.builder()
        .feeCode("COM")
        .startDate(LocalDate.of(2025, 5, 12))
        .vatIndicator(false)
        .netDisbursementAmount(0.0)
        .disbursementVatAmount(0.0)
        .caseConcludedDate(LocalDate.of(2026, 1, 30))
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("COM")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("COM_FS2013").build())
        .fixedFee(new BigDecimal("50.00"))
        .categoryType(COMMUNITY_CARE)
        .build();

    FeeCalculationResponse response = calculator.calculate(request, feeEntity);

    assertThat(response.getEscapeCaseFlag()).isFalse();
    assertThat(response.getValidationMessages()).isEmpty();
  }
}
