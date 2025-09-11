package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;

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
import uk.gov.justice.laa.fee.scheme.feecalculator.fixed.PoliceStationFixedFeeCalculator;
import uk.gov.justice.laa.fee.scheme.feecalculator.hourly.PoliceStationHourlyFeeCalculator;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.service.DataService;

@ExtendWith(MockitoExtension.class)
class PoliceStationFeeCalculatorTest {

  @InjectMocks
  PoliceStationFeeCalculator policeStationFeeCalculator;

  @Mock
  DataService dataService;

  @Mock
  PoliceStationFixedFeeCalculator policeStationFixedFeeCalculator;

  @Mock
  PoliceStationHourlyFeeCalculator policeStationHourlyFeeCalculator;

  @Test
  void getFee_whenPoliceStationFeeFixed_shouldReturnFeeCalculationResponse() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVC")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(45.0)
        .netProfitCosts(676.0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVC")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("I&A_FS2023").build())
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
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();



    when(dataService.getFeeEntity(any())).thenReturn(feeEntity);
    when(policeStationFixedFeeCalculator.getFee(feeEntity,feeCalculationRequest)).thenReturn(expectedResponse);

    FeeCalculationResponse result = policeStationFeeCalculator.calculate(feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVC");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

  @Test
  void getFee_whenPoliceStationFeeHourly_shouldReturnFeeCalculationResponse() {


    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("INVK")
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(true)
        .policeStationSchemeId("1001")
        .policeStationId("NE001")
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber("121222/4523")
        .travelAndWaitingCosts(23.00)
        .netProfitCosts(450.90)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("INVK")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("POL_FS2022").build())
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
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();


    when(dataService.getFeeEntity(any())).thenReturn(feeEntity);
    when(policeStationHourlyFeeCalculator.getFee(feeEntity,feeCalculationRequest)).thenReturn(expectedResponse);


    FeeCalculationResponse result = policeStationFeeCalculator.calculate(feeCalculationRequest);

    assertThat(result).isNotNull();
    assertThat(result.getFeeCode()).isEqualTo("INVK");
    assertThat(result.getFeeCalculation()).isNotNull();
    assertThat(result.getFeeCalculation().getTotalAmount()).isEqualTo(311.32);
  }

}