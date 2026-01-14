package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.POLICE_STATION;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_POLICE_OTHER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class PoliceStationHourlyRateCalculatorTest extends BaseFeeCalculatorTest {

  private static final String UFN = "041223/665";

  @InjectMocks
  PoliceStationHourlyRateCalculator policeStationHourlyRateCalculator;


  public static Stream<Arguments> testPoliceOtherData() {

    LocalDate start2016 = LocalDate.of(2016, 4, 15);
    LocalDate start2021 = LocalDate.of(2021, 7, 10);
    LocalDate start2022 = LocalDate.of(2022, 5, 20);

    return Stream.of(

        // ---------- INVA ----------
        arguments("INVA, VAT applied", "INVA", true,
            start2016, "POL_FS2016",
            100.0, 60.0, 12.0, 20.0, 10.0,
            240.0, 38.0, 190.0),

        arguments("INVA, VAT NOT applied", "INVA", false,
            start2022, "POL_FS2022",
            100.0, 60.0, 12.0, 20.0, 10.0,
            202.0, 0.0, 190.0),

        // ---------- INVE ----------
        arguments("INVE, VAT applied", "INVE", true,
            start2016, "POL_FS2016",
            140.0, 70.0, 14.0, 15.0, 8.0,
            293.6, 46.6, 233.0),

        arguments("INVE, VAT NOT applied", "INVE", false,
            start2022, "POL_FS2022",
            140.0, 70.0, 14.0, 15.0, 8.0,
            247.0, 0.0, 233.0),

        // ---------- INVH ----------
        arguments("INVH, VAT applied", "INVH", true,
            start2016, "POL_FS2016",
            160.0, 85.0, 17.0, 22.0, 6.0,
            344.6, 54.6, 273.0),

        arguments("INVH, VAT NOT applied", "INVH", false,
            start2022, "POL_FS2022",
            160.0, 85.0, 17.0, 22.0, 6.0,
            290.0, 0.0, 273.0),

        // ---------- INVK ----------
        arguments("INVK, VAT applied", "INVK", true,
            start2016, "POL_FS2016",
            130.0, 75.0, 15.0, 18.0, 7.0,
            291.0, 46.0, 230.0),

        arguments("INVK, VAT NOT applied", "INVK", false,
            start2022, "POL_FS2022",
            130.0, 75.0, 15.0, 18.0, 7.0,
            245.0, 0.0, 230.0),

        // ---------- INVL ----------
        arguments("INVL, VAT applied", "INVL", true,
            start2016, "POL_FS2016",
            180.0, 95.0, 19.0, 20.0, 11.0,
            386.2, 61.2, 306.0),

        arguments("INVL, VAT NOT applied", "INVL", false,
            start2022, "POL_FS2022",
            180.0, 95.0, 19.0, 20.0, 11.0,
            325.0, 0.0, 306.0),

        // ---------- INVM (Special Schemes Only) ----------
        arguments("INVM, VAT applied (FS2021)", "INVM", true,
            start2021, "POL_FS2021",
            150.0, 110.0, 22.0, 19.0, 9.0,
            367.6, 57.6, 288.0),

        arguments("INVM, VAT NOT applied (FS2022)", "INVM", false,
            start2022, "POL_FS2022",
            150.0, 110.0, 22.0, 19.0, 9.0,
            310.0, 0.0, 288.0)
    );
  }



  private static Arguments arguments(String testDescription,
                                     String feeCode,
                                     boolean vatIndicator,
                                     LocalDate startDate,
                                     String feeSchemeCode,
                                     double netProfitCosts,
                                     double disbursementAmount,
                                     double disbursementVatAmount,
                                     double netTravelCosts,
                                     double netWaitingCosts,
                                     double expectedTotal,
                                     double expectedCalculatedVat,
                                     double expectedHourlyTotalAmount) {

    return Arguments.of(
        testDescription,
        feeCode,
        vatIndicator,
        startDate,
        feeSchemeCode,
        netProfitCosts,
        disbursementAmount,
        disbursementVatAmount,
        netTravelCosts,
        netWaitingCosts,
        expectedTotal,
        expectedCalculatedVat,
        expectedHourlyTotalAmount
    );
  }


  @ParameterizedTest
  @MethodSource("testPoliceOtherData")
  void test_whenPoliceStation_shouldReturnFee(
      String description,
      String feeCode,
      boolean inputVatIndicator,
      LocalDate startDate,
      String feeSchemeCode,
      double inputNetProfitCosts,
      double inputDisbursementAmount,
      double inputDisbursementVatAmount,
      double inputNetTravelCosts,
      double inputNetWaitingCosts,
      double expectedTotal,
      double expectedCalculatedVat,
      double expectedHourlyTotalAmount
  ) {

    mockVatRatesService(inputVatIndicator);

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(startDate)
        .vatIndicator(inputVatIndicator)
        .netProfitCosts(inputNetProfitCosts)
        .netDisbursementAmount(inputDisbursementAmount)
        .disbursementVatAmount(inputDisbursementVatAmount)
        .netTravelCosts(inputNetTravelCosts)
        .netWaitingCosts(inputNetWaitingCosts)
        .uniqueFileNumber(UFN)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .upperCostLimit(BigDecimal.valueOf(100000.00))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.HOURLY)
        .build();

    FeeCalculationResponse response = policeStationHourlyRateCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(inputVatIndicator)
        .vatRateApplied(inputVatIndicator ? 20.0 : null)
        .disbursementAmount(inputDisbursementAmount)
        .requestedNetDisbursementAmount(inputDisbursementAmount)
        .disbursementVatAmount(inputDisbursementVatAmount)
        .calculatedVatAmount(expectedCalculatedVat)
        .netProfitCostsAmount(inputNetProfitCosts)
        .requestedNetProfitCostsAmount(inputNetProfitCosts)
        .hourlyTotalAmount(expectedHourlyTotalAmount)
        .netTravelCostsAmount(inputNetTravelCosts)
        .netWaitingCostsAmount(inputNetWaitingCosts)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
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
      String feeCode,
      boolean inputVatIndicator,
      LocalDate startDate,
      String feeSchemeCode,
      double inputNetProfitCosts,
      double inputDisbursementAmount,
      double inputDisbursementVatAmount,
      double inputNetTravelCosts,
      double inputNetWaitingCosts,
      double expectedTotal,
      double expectedCalculatedVat,
      double expectedHourlyTotalAmount
  ) {

    mockVatRatesService(inputVatIndicator);

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(startDate)
        .vatIndicator(inputVatIndicator)
        .netProfitCosts(inputNetProfitCosts)
        .netDisbursementAmount(inputDisbursementAmount)
        .disbursementVatAmount(inputDisbursementVatAmount)
        .netTravelCosts(inputNetTravelCosts)
        .netWaitingCosts(inputNetWaitingCosts)
        .uniqueFileNumber(UFN)
        .build();

    FeeSchemesEntity feeSchemesEntity = FeeSchemesEntity.builder().schemeCode(feeSchemeCode).build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(feeSchemesEntity)
        .upperCostLimit(new BigDecimal("25.0"))
        .categoryType(POLICE_STATION)
        .feeType(FeeType.HOURLY)
        .build();

    FeeCalculationResponse response = policeStationHourlyRateCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(inputVatIndicator)
        .vatRateApplied(inputVatIndicator ? 20.0 : null)
        .disbursementAmount(inputDisbursementAmount)
        .requestedNetDisbursementAmount(inputDisbursementAmount)
        .disbursementVatAmount(inputDisbursementVatAmount)
        .calculatedVatAmount(expectedCalculatedVat)
        .netProfitCostsAmount(inputNetProfitCosts)
        .requestedNetProfitCostsAmount(inputNetProfitCosts)
        .hourlyTotalAmount(expectedHourlyTotalAmount)
        .netTravelCostsAmount(inputNetTravelCosts)
        .netWaitingCostsAmount(inputNetWaitingCosts)
        .build();
    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
        .code(WARN_POLICE_OTHER_UPPER_LIMIT.getCode())
        .message(WARN_POLICE_OTHER_UPPER_LIMIT.getMessage())
        .type(WARNING)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId(feeSchemeCode)
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
}
