package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.ADVOCACY_APPEALS_REVIEWS;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;
import static uk.gov.justice.laa.fee.scheme.feecalculator.hourly.AdvocacyAppealsReviewsHourlyRateCalculator.WARNING_CODE_DESCRIPTION;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class AdvocacyAppealsReviewsHourlyRateCalculatorTest {

  @InjectMocks
  AdvocacyAppealsReviewsHourlyRateCalculator calculator;


  public static Stream<Arguments> testDataWithDisbursement() {
    return Stream.of(
        arguments("PROH, with VAT, no warning", "PROH", true, 100, 20, 500,
            50, 50, 1574.06, 120.0, 600, 840, false),
        arguments("APPA, with VAT, no warning", "APPA", true, 40, 5, 100,
            50, 50, 314.81, 40, 200, 285, false),
        arguments("APPB, with VAT, no warning", "APPB", true, 50, 10, 210,
            50, 50, 524, 62, 310, 432, false),
        arguments("PROH, no VAT, no warning", "PROH", false, 100, 20, 500,
            50, 50, 1574.06, 0, 600, 720, false),
        arguments("APPA, no VAT, no warning", "APPA", false, 40, 5, 100,
            50, 50, 314.81, 0, 200, 245, false),
        arguments("APPB, no VAT, no warning", "APPB", false, 50, 10, 210,
            50, 50, 524, 0, 310, 370, false),
        arguments("PROH, with VAT, has warning", "PROH", true, 100, 20, 1200,
            100, 200, 1574.06, 300, 1500, 1920, true),
        arguments("APPA, no VAT, has warning", "APPA", false, 40, 5, 200,
            50, 50, 314.81, 0, 300, 345, true)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat,
                                     double netDisbursementAmount, double disbursementVatAmount, double requestedProfitCosts,
                                     double requestedTravelCosts, double requestedWaitingCosts, double upperCostLimit,
                                     double calculatedVatAmount, double hourlyTotalAmount, double expectedTotal, boolean hasWarning) {
    return Arguments.of(scenario, feeCode, vat, netDisbursementAmount, disbursementVatAmount, requestedProfitCosts,
        requestedTravelCosts, requestedWaitingCosts, upperCostLimit, calculatedVatAmount, hourlyTotalAmount,
        expectedTotal, hasWarning);
  }

  @ParameterizedTest
  @MethodSource("testDataWithDisbursement")
  void calculate_whenImmigrationAndAsylum_withDisbursement(
      String description,
      String feeCode,
      boolean vatIndicator,
      double netDisbursementAmount,
      double disbursementVatAmount,
      double requestedProfitCosts,
      double requestedTravelCosts,
      double requestedWaitingCosts,
      double upperCostLimit,
      double calculatedVatAmount,
      double hourlyTotalAmount,
      double expectedTotal,
      boolean hasWarning
  ) {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .uniqueFileNumber("110425/abc")
        .netProfitCosts(requestedProfitCosts)
        .netDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .vatIndicator(vatIndicator)
        .netTravelCosts(requestedTravelCosts)
        .netWaitingCosts(requestedWaitingCosts)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("AAR_FS2022").build())
        .categoryType(ADVOCACY_APPEALS_REVIEWS)
        .feeType(HOURLY)
        .totalLimit(new BigDecimal(upperCostLimit))
        .build();

    FeeCalculationResponse response = calculator.calculate(feeCalculationRequest, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(20.0)
        .calculatedVatAmount(calculatedVatAmount)
        .disbursementAmount(netDisbursementAmount)
        .requestedNetDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .hourlyTotalAmount(hourlyTotalAmount)
        .netProfitCostsAmount(requestedProfitCosts)
        .requestedNetProfitCostsAmount(requestedProfitCosts)
        .netTravelCostsAmount(requestedTravelCosts)
        .netWaitingCosts(requestedWaitingCosts)
        .build();

    ValidationMessagesInner warning = ValidationMessagesInner.builder().message(WARNING_CODE_DESCRIPTION).type(WARNING).build();
    List<ValidationMessagesInner> validationMessage = new ArrayList<>();
    validationMessage.add(warning);

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("AAR_FS2022")
        .claimId("claim_123")
        .validationMessages(hasWarning ? validationMessage : new ArrayList<>())
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

}