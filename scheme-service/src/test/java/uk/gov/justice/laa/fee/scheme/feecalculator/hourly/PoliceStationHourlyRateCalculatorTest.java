package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_POLICE_OTHER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;
import uk.gov.justice.laa.fee.scheme.service.VatRatesService;

@ExtendWith(MockitoExtension.class)
class PoliceStationHourlyRateCalculatorTest {

  private static final String FEE_CODE = "INVM";
  private static final String FEE_SCHEME_CODE = "POL_2023";
  private static final String POLICE_STATION_ID = "NE024";
  private static final String POLICE_STATION_SCHEME_ID = "1007";
  private static final String UFN = "041223/665";

  @Mock
  VatRatesService vatRatesService;

  @InjectMocks
  PoliceStationHourlyRateCalculator policeStationHourlyRateCalculator;

  public static Stream<Arguments> testPoliceOtherData() {
    return Stream.of(
        arguments("INVM Police Fee Code, VAT applied", true, 232.72,
            27.01, 135.06),
        arguments("INVM Police Fee Code, VAT applied", false, 205.71,
            0, 135.06)
    );
  }

  private static Arguments arguments(String testDescription,
                                     boolean vatIndicator,
                                     double expectedTotal,
                                     double expectedCalculatedVat,
                                     double expectedHourlyTotalAmount) {
    return Arguments.of(testDescription, vatIndicator,
        expectedTotal, expectedCalculatedVat, expectedHourlyTotalAmount);
  }

  @ParameterizedTest
  @MethodSource("testPoliceOtherData")
  void test_whenPoliceStation_shouldReturnFee(
      String description,
      boolean vatIndicator,
      double expectedTotal,
      double expectedCalculatedVat,
      double expectedHourlyTotalAmount
  ) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(FEE_CODE)
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(POLICE_STATION_SCHEME_ID)
        .policeStationId(POLICE_STATION_ID)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .netTravelCosts(30.00)
        .netWaitingCosts(20.00)
        .uniqueFileNumber(UFN)
        .netProfitCosts(34.56)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(FEE_SCHEME_CODE).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(FEE_CODE)
        .feeScheme(feeSchemesEntity)
        .upperCostLimit(BigDecimal.valueOf(100000.00))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.HOURLY)
        .build();

    FeeCalculationResponse response = policeStationHourlyRateCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .disbursementAmount(50.5)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .calculatedVatAmount(expectedCalculatedVat)
        .netProfitCostsAmount(34.56)
        .requestedNetProfitCostsAmount(34.56)
        .hourlyTotalAmount(expectedHourlyTotalAmount)
        .netTravelCostsAmount(30.00)
        .netWaitingCostsAmount(20.00)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(FEE_CODE)
        .schemeId(FEE_SCHEME_CODE)
        .validationMessages(new ArrayList<>())
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @MethodSource("testPoliceOtherData")
  void test_whenPoliceStation_shouldReturnFeeWithWarning(
      String description,
      boolean vatIndicator,
      double expectedTotal,
      double expectedCalculatedVat,
      double expectedHourlyTotalAmount
  ) {

    mockVatRatesService(vatIndicator);

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(FEE_CODE)
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(POLICE_STATION_SCHEME_ID)
        .policeStationId(POLICE_STATION_ID)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .netTravelCosts(30.00)
        .netWaitingCosts(20.00)
        .uniqueFileNumber(UFN)
        .netProfitCosts(34.56)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(FEE_SCHEME_CODE).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(FEE_CODE)
        .feeScheme(feeSchemesEntity)
        .upperCostLimit(new BigDecimal("25.0"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.HOURLY)
        .build();

    FeeCalculationResponse response = policeStationHourlyRateCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .disbursementAmount(50.5)
        .requestedNetDisbursementAmount(50.5)
        .disbursementVatAmount(20.15)
        .calculatedVatAmount(expectedCalculatedVat)
        .netProfitCostsAmount(34.56)
        .requestedNetProfitCostsAmount(34.56)
        .hourlyTotalAmount(expectedHourlyTotalAmount)
        .netTravelCostsAmount(30.00)
        .netWaitingCostsAmount(20.00)
        .build();
    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .code(WARN_POLICE_OTHER_UPPER_LIMIT.getCode())
        .message(WARN_POLICE_OTHER_UPPER_LIMIT.getMessage())
        .type(WARNING)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(FEE_CODE)
        .schemeId(FEE_SCHEME_CODE)
        .validationMessages(List.of(validationMessage))
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }


  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = policeStationHourlyRateCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }

  private void mockVatRatesService(Boolean vatIndicator) {
    when(vatRatesService.getVatRateForDate(any(), any()))
        .thenReturn(vatIndicator ? new BigDecimal("20.00") : BigDecimal.ZERO);
  }
}
