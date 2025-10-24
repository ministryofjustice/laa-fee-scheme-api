package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRE_ORDER_COVER;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_ALL_FEE_CODE;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;
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
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class PreOrderCoverHourlyRateCalculatorTest {

  @InjectMocks
  PreOrderCoverHourlyRateCalculator preOrderCoverHourlyRateCalculator;


  public static Stream<Arguments> testDataWithDisbursement() {
    return Stream.of(
        arguments("PROP1, no error", "PROP1", true, 10.0, 2.0, 10,
            10, 10, 55.14, 6.0, 30.0, 48.0, false),
//        arguments("PROP2, no error", "PROP2", true, 40, 5, 100,
//            50, 50, 314.81, 40, 200, 285, false),
//        arguments("PROP1, has error", "PROP1", true, 50, 10, 210,
//            50, 50, 524, 62, 310, 432, true),
        arguments("PROP1, no error", "PROP1", true, 10.0, 2.0, 60,
            10, 10, 55.14, 6.0, 30.0, 48.0, true)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat,
                                     double netDisbursementAmount, double disbursementVatAmount, double requestedProfitCosts,
                                     double requestedTravelCosts, double requestedWaitingCosts, double upperCostLimit,
                                     double calculatedVatAmount, double hourlyTotalAmount, double expectedTotal, boolean hasError) {
    return Arguments.of(scenario, feeCode, vat, netDisbursementAmount, disbursementVatAmount, requestedProfitCosts,
        requestedTravelCosts, requestedWaitingCosts, upperCostLimit, calculatedVatAmount, hourlyTotalAmount,
        expectedTotal, hasError);
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
      boolean hasError
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
        .feeScheme(FeeSchemesEntity.builder().schemeCode("POC_FS2022").build())
        .categoryType(PRE_ORDER_COVER)
        .feeType(HOURLY)
        .totalLimit(new BigDecimal(upperCostLimit))
        .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .calculatedVatAmount(calculatedVatAmount)
        .disbursementAmount(netDisbursementAmount)
        .requestedNetDisbursementAmount(netDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .hourlyTotalAmount(hourlyTotalAmount)
        .netProfitCostsAmount(requestedProfitCosts)
        .requestedNetProfitCostsAmount(requestedProfitCosts)
        .netTravelCostsAmount(requestedTravelCosts)
        .netWaitingCostsAmount(requestedWaitingCosts)
        .build();

    if (hasError) {
      assertThatThrownBy(() -> preOrderCoverHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity))
          .isInstanceOf(ValidationException.class)
          .hasFieldOrPropertyWithValue("error", ERR_ALL_FEE_CODE)
          .hasMessageContaining(ERR_ALL_FEE_CODE.getMessage());
    } else {
      FeeCalculationResponse response = preOrderCoverHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);
      FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
          .feeCode(feeCode)
          .schemeId("POC_FS2022")
          .claimId("claim_123")
          .validationMessages(new ArrayList<>())
          .feeCalculation(expectedCalculation)
          .build();

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }


  }

}