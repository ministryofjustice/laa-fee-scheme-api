package uk.gov.justice.laa.fee.scheme.feecalculator.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.EDUCATION;
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
import uk.gov.justice.laa.fee.scheme.feecalculator.EducationFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.disbursement.EducationDisbursementOnlyCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.EducationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class EducationFeeCalculatorTest {

  @InjectMocks
  EducationFeeCalculator educationFeeCalculator;

  @Mock
  EducationFixedFeeCalculator educationFixedFeeCalculator;

  @Mock
  EducationDisbursementOnlyCalculator educationDisbursementOnlyCalculator;

  @Test
  void getFee_whenEducationFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUFIN")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUFIN")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("EDU_FS2013").build())
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
        .feeCode("EDUFIN")
        .schemeId("EDU_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(educationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("EDUFIN");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenEducationFeeDisbursement_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUDIS")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUDIS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("EDU_FS2013").build())
        .categoryType(MENTAL_HEALTH)
        .feeType(DISB_ONLY)
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUDIS")
        .schemeId("EDU_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(educationDisbursementOnlyCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = educationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("EDUDIS");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void calculate_shouldThrowIllegalStateException_whenFeeTypeIsHourly() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUDIS")
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUDIS")
        .feeType(HOURLY)
        .build();

    assertThatThrownBy(() -> educationFeeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(UnsupportedOperationException.class)
        .hasMessage("Hourly rate fee is not supported for Education category.");
  }

  @Test
  void getSupportedCategories_shouldReturnEducationCategory() {
    Set<CategoryType> result = educationFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(EDUCATION));
  }
}