package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.type.CategoryType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.feecalculator.type.FeeType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class PoliceStationHourlyFeeCalculatorTest {

  public static Stream<Arguments> testPoliceOtherData() {
    return Stream.of(
        arguments("INVM Police Fee Code, VAT applied", "INVM", "NE024",
            "1007", "041223/6655", true, 187.66,
            null, new BigDecimal("25.0"), "POL_2023", 19.5,
            50.5, 20.15, 0, 12.45,
            34.56, 97.51),
        arguments("INVM Police Fee Code, VAT applied", "INVM", "NE024",
            "1007", "041223/6655", false, 168.16,
            null, new BigDecimal("25.0"), "POL_2023", 0,
            50.5, 20.15, 0, 12.45,
            34.56, 97.51)
    );
  }


  private static Arguments arguments(String testDescription,
                                     String feeCode,
                                     String policeStationId,
                                     String policeStationSchemeId,
                                     String uniqueFileNumber,
                                     boolean vatIndicator,
                                     double expectedTotal,
                                     BigDecimal fixedFee,
                                     BigDecimal profitCostLimit,
                                     String feeSchemeCode,
                                     double expectedCalculatedVat,
                                     double disbursementAmount,
                                     double disbursementVatAmount,
                                     double fixedFeeAmount,
                                     double travelAndWaitingCostAmount,
                                     double netProfitCostsAmount,
                                     double hourlyTotalAmount) {
    return Arguments.of(testDescription, feeCode, policeStationId, policeStationSchemeId, uniqueFileNumber, vatIndicator,
        expectedTotal, fixedFee, profitCostLimit, feeSchemeCode, expectedCalculatedVat, disbursementAmount,
        disbursementVatAmount, fixedFeeAmount, travelAndWaitingCostAmount, netProfitCostsAmount, hourlyTotalAmount);
  }

  @ParameterizedTest
  @MethodSource("testPoliceOtherData")
  void test_whenPoliceStation_shouldReturnFee(
      String description,
      String feeCode,
      String policeStationId,
      String policeStationSchemeId,
      String uniqueFileNumber,
      boolean vatIndicator,
      double expectedTotal,
      BigDecimal fixedFee,
      BigDecimal profitCostLimit,
      String feeSchemeCode,
      double expectedCalculatedVat,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee,
      double travelAndWaitingCostAmount,
      double netProfitCostsAmount,
      double hourlyTotalAmount
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .uniqueFileNumber(uniqueFileNumber)
        .travelAndWaitingCosts(travelAndWaitingCostAmount)
        .netProfitCosts(netProfitCostsAmount)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(feeSchemesEntity)
        .profitCostLimit(profitCostLimit)
        .fixedFee(fixedFee)
        .categoryType(POLICE_STATION)
        .feeType(FeeType.HOURLY)
        .build();

    FeeCalculationResponse response = PoliceStationHourlyFeeCalculator.getFee(feeEntity, feeData);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .requestedNetDisbursementAmount(expectedDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .calculatedVatAmount(expectedCalculatedVat)
        .netProfitCostsAmount(netProfitCostsAmount)
        .requestedNetProfitCostsAmount(netProfitCostsAmount)
        .hourlyTotalAmount(hourlyTotalAmount)
        .travelAndWaitingCostAmount(travelAndWaitingCostAmount)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .warnings(List.of("warning net profit costs"))
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

}
