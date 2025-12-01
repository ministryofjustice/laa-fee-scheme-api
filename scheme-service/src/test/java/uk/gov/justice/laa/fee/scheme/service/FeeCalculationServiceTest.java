package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CaseType.CIVIL;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAsylumFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class FeeCalculationServiceTest {

  @Mock
  FeeCalculatorFactory feeCalculatorFactory;

  @Mock
  FeeDataService feeDataService;

  @Mock
  FeeDetailsService feeDetailsService;

  @Mock
  ValidationService validationService;

  @Mock
  ImmigrationAsylumFeeCalculator immigrationCalculator;

  @InjectMocks
  private FeeCalculationService feeCalculationService;

  @Test
  void calculateFee_returnsCalculationResponse() {

    // Arrange
    CategoryType category = IMMIGRATION_ASYLUM;
    FeeCalculationRequest feeCalculationRequest = buildFeeCalculationRequest();
    FeeEntity feeEntity = buildFeeEntity();
    List<FeeEntity> feeEntityList = List.of(feeEntity);

    when(feeDataService.getFeeEntities("FEE123")).thenReturn(feeEntityList);
    when(feeDetailsService.getCaseType(feeCalculationRequest)).thenReturn(CIVIL);
    when(validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest, CIVIL)).thenReturn(feeEntity);
    when(feeCalculatorFactory.getCalculator(category)).thenReturn(immigrationCalculator);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1587.50)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(800.0)
        .disbursementVatAmount(40.0)
        .fixedFeeAmount(456.00)
        .calculatedVatAmount(236.00)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();

    when(immigrationCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse response = feeCalculationService.calculateFee(feeCalculationRequest);

    assertFeeCalculation(response);

    verify(feeCalculatorFactory, times(1)).getCalculator(category);

    verify(immigrationCalculator).calculate(feeCalculationRequest, feeEntity);
  }

  @Test
  void calculateFee_returnsCalculationResponseWithWarnings() {

    // Arrange
    CategoryType category = IMMIGRATION_ASYLUM;
    FeeCalculationRequest feeCalculationRequest = buildFeeCalculationRequest();
    FeeEntity feeEntity = buildFeeEntity();
    List<FeeEntity> feeEntityList = List.of(feeEntity);

    when(feeDataService.getFeeEntities("FEE123")).thenReturn(feeEntityList);
    when(feeDetailsService.getCaseType(feeCalculationRequest)).thenReturn(CIVIL);
    when(validationService.getValidFeeEntity(feeEntityList, feeCalculationRequest, CIVIL)).thenReturn(feeEntity);
    when(feeCalculatorFactory.getCalculator(category)).thenReturn(immigrationCalculator);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(1587.50)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(800.0)
        .disbursementVatAmount(40.0)
        .fixedFeeAmount(456.00)
        .calculatedVatAmount(236.00)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("FEE123")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .validationMessages(List.of(ValidationMessagesInner.builder()
                .type(ValidationMessagesInner.TypeEnum.WARNING)
                .code("WARCIV1")
                .message("error message")
            .build()))
        .build();
    when(immigrationCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse response = feeCalculationService.calculateFee(feeCalculationRequest);

    assertThat(response).isEqualTo(expectedResponse);

    verify(feeCalculatorFactory, times(1)).getCalculator(category);

    verify(immigrationCalculator).calculate(feeCalculationRequest, feeEntity);
  }

  private static FeeEntity buildFeeEntity() {
    return FeeEntity.builder()
        .feeCode("FEE123")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .categoryType(IMMIGRATION_ASYLUM)
        .escapeThresholdLimit(new BigDecimal("700.00"))
        .build();
  }

  private static FeeCalculationRequest buildFeeCalculationRequest() {
    return FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();
  }

  private void assertFeeCalculation(FeeCalculationResponse response) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE123");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(1587.5);
  }

}