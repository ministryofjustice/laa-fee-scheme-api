package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyRateCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

@ExtendWith(MockitoExtension.class)
class PoliceStationFeeCalculatorTest {

  @InjectMocks
  PoliceStationFeeCalculator policeStationFeeCalculator;

  @Mock
  PoliceStationFixedFeeCalculator policeStationFixedFeeCalculator;

  @Mock
  PoliceStationHourlyRateCalculator policeStationHourlyRateCalculator;

  @Test
  void getFee_whenPoliceStationFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
        .fixedFee(new BigDecimal("75.50"))
        .categoryType(POLICE_STATION)
        .feeType(FIXED)
        .disbursementLimit(new BigDecimal(435))
        .oralCmrhBoltOn(BigDecimal.valueOf(166))
        .telephoneCmrhBoltOn(BigDecimal.valueOf(90))
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
        .feeCode("INVC")
        .schemeId("POL_FS2022")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(policeStationFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);

    FeeCalculationResponse result = policeStationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenPoliceStationFeeHourly_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVK")
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/452")
        .travelAndWaitingCosts(23.00)
        .netProfitCosts(450.90)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVK")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("POL_FS2022").build())
        .categoryType(POLICE_STATION)
        .feeType(HOURLY)
        .profitCostLimit(new BigDecimal("800.00"))
        .disbursementLimit(new BigDecimal("400.00"))
        .build();


    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(311.32)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("INVK")
        .schemeId("POL_FS2022")
        .validationMessages(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    when(policeStationHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity)).thenReturn(expectedResponse);


    FeeCalculationResponse result = policeStationFeeCalculator.calculate(feeCalculationRequest, feeEntity);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVK");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getSupportedCategories_shouldReturnPoliceStationCategory() {
    Set<CategoryType> result = policeStationFeeCalculator.getSupportedCategories();

    assertThat(result).isEqualTo(Set.of(POLICE_STATION));
  }

}