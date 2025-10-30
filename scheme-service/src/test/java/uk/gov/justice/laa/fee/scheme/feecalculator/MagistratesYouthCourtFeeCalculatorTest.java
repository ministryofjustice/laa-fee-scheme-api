package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MAGISTRATES_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.YOUTH_COURT;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.DISB_ONLY;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;

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
import uk.gov.justice.laa.fee.scheme.enums.CourtDesignationType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.DesignatedCourtFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.UndesignatedCourtFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class MagistratesYouthCourtFeeCalculatorTest {

  @InjectMocks
  MagistratesYouthCourtCalculator magistratesYouthCourtCalculator;

  @Mock
  DesignatedCourtFixedFeeCalculator designatedCourtFixedFeeCalculator;

  @Mock
  UndesignatedCourtFixedFeeCalculator undesignatedCourtFixedFeeCalculator;

  @Test
  void getFee_whenMagistratesCourtDesignatedAndFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROL2")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROL2")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MAGS_COURT_FS2016").build())
        .fixedFee(new BigDecimal("435.64"))
        .categoryType(MAGISTRATES_COURT)
        .courtDesignationType(CourtDesignationType.DESIGNATED)
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
        .feeCode("PROL2")
        .schemeId("MAGS_COURT_FS2016")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(designatedCourtFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = magistratesYouthCourtCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("PROL2");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenMentalHealthFeeDisbursement_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("EDUDIS")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("EDUDIS")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .categoryType(MENTAL_HEALTH)
        .feeType(DISB_ONLY)
        .disbursementLimit(new BigDecimal("400.00"))
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("EDUDIS")
        .schemeId("MHL_FS2013")
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(undesignatedCourtFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = magistratesYouthCourtCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("EDUDIS");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getSupportedCategories_shouldReturnMagistratesOrYouthCourtCategory() {
    Set<CategoryType> result = magistratesYouthCourtCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(MAGISTRATES_COURT, YOUTH_COURT));
  }

}