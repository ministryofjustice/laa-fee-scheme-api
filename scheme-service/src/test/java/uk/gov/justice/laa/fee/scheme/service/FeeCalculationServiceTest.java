package uk.gov.justice.laa.fee.scheme.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CRIME;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRISON_LAW;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAsylumFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PrisonLawFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;

@ExtendWith(MockitoExtension.class)
class FeeCalculationServiceTest {

  @Mock
  FeeCalculatorFactory feeCalculatorFactory;

  @Mock
  FeeDataService feeDataService;

  @Mock
  FeeDetailsService feeDetailsService;

  @Mock
  CivilValidationService civilValidationService;

  @Mock
  CrimeValidationService crimeValidationService;

  @Mock
  ImmigrationAsylumFeeCalculator immigrationCalculator;

  @Mock
  PrisonLawFixedFeeCalculator prisonLawFixedFeeCalculator;

  @InjectMocks
  private FeeCalculationService feeCalculationService;

  @Test
  void calculateFee_whenCaseTypeIsCivil_usesCivilValidation() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FEE123")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();

    when(feeDetailsService.getCaseType(feeCalculationRequest)).thenReturn(CIVIL);
    when(feeDataService.getFeeEntities("FEE123")).thenReturn(List.of(feeEntity));
    when(civilValidationService.getValidFeeEntity(List.of(feeEntity), feeCalculationRequest)).thenReturn(feeEntity);
    when(feeCalculatorFactory.getCalculator(IMMIGRATION_ASYLUM)).thenReturn(immigrationCalculator);

    feeCalculationService.calculateFee(feeCalculationRequest);

    verify(civilValidationService).getValidFeeEntity(List.of(feeEntity), feeCalculationRequest);
    verify(feeCalculatorFactory).getCalculator(IMMIGRATION_ASYLUM);
    verify(immigrationCalculator).calculate(feeCalculationRequest, feeEntity);
  }

  @Test
  void calculateFee_whenCaseTypeIsCrime_usesCrimeValidation() {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .uniqueFileNumber("211225/123")
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FEE123")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("PRISON_FS2025").build())
        .categoryType(PRISON_LAW)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();

    when(feeDetailsService.getCaseType(feeCalculationRequest)).thenReturn(CRIME);
    when(feeDataService.getFeeEntities("FEE123")).thenReturn(List.of(feeEntity));
    when(crimeValidationService.getValidFeeEntity(List.of(feeEntity), feeCalculationRequest)).thenReturn(feeEntity);
    when(feeCalculatorFactory.getCalculator(PRISON_LAW)).thenReturn(prisonLawFixedFeeCalculator);

    feeCalculationService.calculateFee(feeCalculationRequest);

    verify(crimeValidationService).getValidFeeEntity(List.of(feeEntity), feeCalculationRequest);
    verify(feeCalculatorFactory).getCalculator(PRISON_LAW);
    verify(prisonLawFixedFeeCalculator).calculate(feeCalculationRequest, feeEntity);
  }

}