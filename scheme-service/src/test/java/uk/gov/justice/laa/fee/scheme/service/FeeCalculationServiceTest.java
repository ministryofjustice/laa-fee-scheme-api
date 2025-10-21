package uk.gov.justice.laa.fee.scheme.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.FeeCalculatorFactory;
import uk.gov.justice.laa.fee.scheme.feecalculator.ImmigrationAsylumFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class FeeCalculationServiceTest {

  @Mock
  FeeCalculatorFactory feeCalculatorFactory;

  @Mock
  FeeDataService feeDataService;

  @Mock
  ValidationService validationService;

  @InjectMocks
  private FeeCalculationService feeCalculationService;

  @Test
  void calculateFee_DelegatesToCalculator() {

    // Picked Immigration Asylum Fee Calculator for stubbing
    FeeCalculator immigrationCalculator = mock(ImmigrationAsylumFeeCalculator.class);
    // Arrange
    CategoryType category = CategoryType.valueOf(IMMIGRATION_ASYLUM.name());
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(70.75)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(2)
        .build();

    FeeEntity feeEntity =  FeeEntity.builder()
          .feeCode("INVC")
          .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
          .categoryType(IMMIGRATION_ASYLUM)
          .escapeThresholdLimit(new BigDecimal("700.00"))
          .build();

    when(feeCalculatorFactory.getCalculator(category)).thenReturn(immigrationCalculator);

    when(validationService.getValidFeeEntity(any(), any())).thenReturn(feeEntity);

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
        .feeCode("INVC")
        .schemeId("IMM_ASYLM_FS2023")
        .claimId("claim_123")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false) // hardcoded till escape logic implemented
        .feeCalculation(expectedCalculation)
        .build();
    when(immigrationCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse response = feeCalculationService.calculateFee(feeCalculationRequest);

    assertFeeCalculation(response, "INVC", 1587.5);

    verify(feeCalculatorFactory, times(1)).getCalculator(category);

    verify(immigrationCalculator).calculate(feeCalculationRequest, feeEntity);
  }



  private void assertFeeCalculation(FeeCalculationResponse response, String feeCode, double total) {
    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo(feeCode);

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(total);
  }

}