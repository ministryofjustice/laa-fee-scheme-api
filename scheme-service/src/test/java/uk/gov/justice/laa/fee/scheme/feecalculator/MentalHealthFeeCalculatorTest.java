package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.MentalHealthDisbursementOnlyCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.MentalHealthFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class MentalHealthFeeCalculatorTest {

  @InjectMocks
  MentalHealthFeeCalculator mentalHealthFeeCalculator;

  @Mock
  MentalHealthFixedFeeCalculator mentalHealthFixedFeeCalculator;

  @Mock
  MentalHealthDisbursementOnlyCalculator mentalHealthDisbursementOnlyCalculator;

  @Test
  void getFee_whenMentalHealthFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHL03")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHL03")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .fixedFee(new BigDecimal("321.0"))
        .categoryType(MENTAL_HEALTH)
        .feeType(FIXED)
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .vatIndicator(Boolean.TRUE)
        .vatRateApplied(20.0)
        .calculatedVatAmount(40.11)
        .disbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .fixedFeeAmount(200.56)
        .calculatedVatAmount(40.11)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("MHL03")
        .schemeId("MHL_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(mentalHealthFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = mentalHealthFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("MHL03");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenMentalHealthFeeDisbursement_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHLDIS")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHLDIS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_DISB_FS2013").build())
        .categoryType(MENTAL_HEALTH)
        .feeType(DISB_ONLY)
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("MHLDIS")
        .schemeId("MHL_DISB_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(mentalHealthDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = mentalHealthFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("MHLDIS");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void calculate_shouldThrowIllegalStateException_whenFeeTypeIsHourlyRate() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHL03")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHL03")
        .feeType(HOURLY)
        .build();

    assertThatThrownBy(() -> mentalHealthFeeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Hourly rate fee is not supported for Mental Health category.");
  }

  @Test
  void getSupportedCategories_shouldReturnMentalHealthCategory() {
    Set<CategoryType> result = mentalHealthFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(MENTAL_HEALTH));
  }

}