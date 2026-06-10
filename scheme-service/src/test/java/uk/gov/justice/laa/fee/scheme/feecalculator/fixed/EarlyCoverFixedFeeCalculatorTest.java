package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class EarlyCoverFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  EarlyCoverFixedFeeCalculator earlyCoverFixedFeeCalculator;

  @ParameterizedTest
  @CsvSource({
      "PROT, 150.00, false, 150.00, 0, EARLY_COVER",
      "PROT, 150.00, true, 180.00, 30.00, EARLY_COVER",
      "PROU, 150.00, false, 150.00, 0, REFUSED_MEANS_TEST",
      "PROU, 150.00, true, 180.00, 30.00, REFUSED_MEANS_TEST"
  })
  void calculate_shouldReturnFeeCalculationResponse(String feeCode, double fixedFees, boolean vatIndicator,
                                                    double expectedTotal, double expectedVat, String categoryType) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .uniqueFileNumber("121222/452")
        .vatIndicator(vatIndicator)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("EC_RMT_FS2022").build())
        .fixedFee(BigDecimal.valueOf(fixedFees))
        .categoryType(CategoryType.valueOf(categoryType))
        .build();

    FeeCalculationResponse result = earlyCoverFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertFeeCalculation(result, feeCode, fixedFees, expectedTotal, vatIndicator, expectedVat);
  }

  private void assertFeeCalculation(FeeCalculationResponse response, String feeCode, double fixedFees,

                                    double total, boolean vatIndicator, double vat) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getClaimId()).isEqualTo("claim_123");
    assertThat(response.getSchemeId()).isEqualTo("EC_RMT_FS2022");

    FeeCalculation feeCalculation = response.getFeeCalculation();
    assertThat(feeCalculation).isNotNull();
    assertThat(feeCalculation.getTotalAmount()).isEqualTo(total);
    assertThat(feeCalculation.getVatIndicator()).isEqualTo(vatIndicator);
    assertThat(feeCalculation.getVatRateApplied()).isEqualTo(vatIndicator ? 20.0 : null);
    assertThat(feeCalculation.getCalculatedVatAmount()).isEqualTo(vat);
    assertThat(feeCalculation.getFixedFeeAmount()).isEqualTo(fixedFees);
  }

  @Test
  void getSupportedCategories_shouldReturnEarlyCoverAndRefusedMeans() {

    Set<CategoryType> result = earlyCoverFixedFeeCalculator.getSupportedCategories();

    assertThat(result).containsExactlyInAnyOrder(CategoryType.EARLY_COVER, CategoryType.REFUSED_MEANS_TEST);
  }
}
