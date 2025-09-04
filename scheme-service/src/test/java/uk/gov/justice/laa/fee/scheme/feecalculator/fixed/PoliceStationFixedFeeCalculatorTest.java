package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class PoliceStationFixedFeeCalculatorTest {

  public static Stream<Arguments> testPoliceStationAttendanceClaims() {
    return Stream.of(
        arguments("INVC Police Fee Code, VAT applied", "INVC", "NE001",
            "1001", "121221/7899", true, 87.93,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVC Police Fee Code, VAT not applied", "INVC", "NE013",
            "1004", "121223/6655", false, 85.05,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4,0.0,0.0)
        );
  }


  public static Stream<Arguments> testPoliceStationTelephonicAdviceClaims() {
    return Stream.of(
        arguments("INVB1 Police Fee Code, VAT applied", "INVB1", "NE001",
            "1001", "121221/7899", true, 87.93,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB1 Police Fee Code, VAT not applied", "INVB1", "NE013",
            "1004", "121223/6655", false, 85.05,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4,0.0,0.0),

        arguments("INVB2 Police Fee Code, VAT applied", "INVB2", "NE001",
            "1001", "121221/7899", true, 87.93,
            new BigDecimal("14.4"), null, "POL_2016", 2.88,
            50.5, 20.15, 14.4, 0.0, 0.0),

        arguments("INVB2 Police Fee Code, VAT not applied", "INVB2", "NE013",
            "1004", "121223/6655", false, 85.05,
            new BigDecimal("14.4"), null, "POL_2023", 0,
            50.5, 20.15, 14.4,0.0,0.0)
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
                                     double netProfitCostsAmount) {
    return Arguments.of(testDescription, feeCode, policeStationId, policeStationSchemeId, uniqueFileNumber, vatIndicator,
        expectedTotal, fixedFee, profitCostLimit, feeSchemeCode, expectedCalculatedVat, disbursementAmount,
        disbursementVatAmount, fixedFeeAmount, travelAndWaitingCostAmount, netProfitCostsAmount);
  }

  @ParameterizedTest
  @MethodSource("testPoliceStationAttendanceClaims")
  void test_whenClaimsSubmittedForPoliceStationAttendance_shouldReturnFee(
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
      double netProfitCostsAmount
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
        .calculationType(POLICE_STATION)
        .feeType("FIXED")
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId(policeStationSchemeId)
        .feeSchemeCode(feeSchemeCode)
        .fixedFee(fixedFee)
        .build();

    FeeCalculationResponse response = PoliceStationFixedFeeCalculator.getFee(feeEntity, policeStationFeesEntity, feeData);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .claimId("temp hardcoded till clarification")
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @MethodSource("testPoliceStationTelephonicAdviceClaims")
  void test_whenClaimsSubmittedForPoliceStationTelephonicAdvice_shouldReturnFee(
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
      double netProfitCostsAmount
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
        .calculationType(POLICE_STATION)
        .feeType("FIXED")
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder()
        .psSchemeId(policeStationSchemeId)
        .feeSchemeCode(feeSchemeCode)
        .fixedFee(fixedFee)
        .build();

    FeeCalculationResponse response = PoliceStationFixedFeeCalculator.getFee(feeEntity, policeStationFeesEntity, feeData);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .disbursementAmount(expectedDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
        .claimId("temp hardcoded till clarification")
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

}
