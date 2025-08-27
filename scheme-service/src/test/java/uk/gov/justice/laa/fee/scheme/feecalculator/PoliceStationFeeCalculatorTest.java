package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.POLICE_STATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.entity.PoliceStationFeesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class PoliceStationFeeCalculatorTest {

  @ParameterizedTest
  @MethodSource("testData")
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
      String feeSchemeCode
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(vatIndicator)
        .policeStationSchemeId(policeStationSchemeId)
        .policeStationId(policeStationId)
        .uniqueFileNumber(uniqueFileNumber)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(feeSchemesEntity)
        .profitCostLimit(profitCostLimit)
        .fixedFee(fixedFee)
        .calculationType(POLICE_STATION)
        .build();

    PoliceStationFeesEntity policeStationFeesEntity = PoliceStationFeesEntity.builder().
                                                          psSchemeId(policeStationSchemeId).
                                                          feeSchemeCode(feeSchemeCode).
                                                          fixedFee(fixedFee).
                                                          build();

    FeeCalculationResponse response = PoliceStationFeeCalculator.getFee(feeEntity,policeStationFeesEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("INVC Police Fee Code, VAT applied",  "INVC", "NE001","1001","121216/7899",true,17.28,new BigDecimal("14.4"),null,"POL_2016"),
        arguments("INVC Police Fee Code, VAT not applied",  "INVC", "NE013","1004","121223/6655",false,14.4,new BigDecimal("14.4"),null,"POL_2023"),
        arguments("INVM Police Fee Code, VAT applied",  "INVM", "NE024","1007","041223/6655",true,30.0,null,new BigDecimal("25.0"),"POL_2023")
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
                                     String feeSchemeCode) {
    return Arguments.of(testDescription, feeCode, policeStationId, policeStationSchemeId, uniqueFileNumber, vatIndicator, expectedTotal,fixedFee,profitCostLimit,feeSchemeCode);
  }

}
