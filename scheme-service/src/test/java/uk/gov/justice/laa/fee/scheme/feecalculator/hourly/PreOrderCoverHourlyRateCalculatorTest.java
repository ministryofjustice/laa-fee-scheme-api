package uk.gov.justice.laa.fee.scheme.feecalculator.hourly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRE_ORDER_COVER;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_CRIME_PREORDER_COVER_UPPER_LIMIT;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.HOURLY;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_DISBURSEMENT_VAT_EXCEEDED;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class PreOrderCoverHourlyRateCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  PreOrderCoverHourlyRateCalculator preOrderCoverHourlyRateCalculator;

  public static Stream<Arguments> testData() {
    return Stream.of(
        // vatIndicator=true, within upper cost limit
        arguments("PROP1, VAT applied, no error", "PROP1", true, 10.0, 2.0, 10,
            10, 10, 55.14, 6.0, 30.0, 48.0, false),
        arguments("PROP2, VAT applied, no error", "PROP1", true, 10.0, 2.0, 10,
            10, 10, 52.14, 6.0, 30.0, 48.0, false),
        // vatIndicator=false, within upper cost limit
        arguments("PROP1, no VAT, no error", "PROP1", false, 10.0, 2.0, 10,
            10, 10, 55.14, 0.0, 30.0, 42.0, false),
        arguments("PROP2, no VAT, no error", "PROP1", false, 10.0, 2.0, 10,
            10, 10, 52.14, 0.0, 30.0, 42.0, false),
        // vatIndicator=true, exceeds upper cost limit → throws error
        arguments("PROP1, has error (upper limit exceeded)", "PROP1", true, 10.0, 2.0, 60,
            10, 10, 55.14, 6.0, 30.0, 48.0, true),
        arguments("PROP1, has error (upper limit exceeded, higher totals)", "PROP1", true, 10.0, 2.0, 60,
            10, 10, 52.14, 6.0, 40.0, 58.0, true)
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
  @MethodSource("testData")
  void calculate_whenPreOrderCover(
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
        .uniqueFileNumber("110425/123")
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
        .upperCostLimit(BigDecimal.valueOf(upperCostLimit))
        .build();

    if (hasError) {
      assertThatThrownBy(() -> preOrderCoverHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity))
          .isInstanceOf(ValidationException.class)
          .hasFieldOrPropertyWithValue("error", ERR_CRIME_PREORDER_COVER_UPPER_LIMIT)
          .hasMessageContaining(ERR_CRIME_PREORDER_COVER_UPPER_LIMIT.getMessage());
    } else {
      mockVatRatesService(vatIndicator);

      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .calculatedVatAmount(calculatedVatAmount)
          .disbursementAmount(netDisbursementAmount)
          .requestedNetDisbursementAmount(netDisbursementAmount)
          .disbursementVatAmount(disbursementVatAmount)
          .requestedDisbursementVatAmount(disbursementVatAmount)
          .hourlyTotalAmount(hourlyTotalAmount)
          .netProfitCostsAmount(requestedProfitCosts)
          .requestedNetProfitCostsAmount(requestedProfitCosts)
          .netTravelCostsAmount(requestedTravelCosts)
          .netWaitingCostsAmount(requestedWaitingCosts)
          .build();

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

  @Test
  void calculate_whenDisbursementVatExceedsMax_shouldCapAndAddWarning() {
    mockVatRatesService(true);

    // netDisbursementAmount=50, vatRate=20% → maxDisbVat=10; submitted disbVat=25 (exceeds)
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("PROP1")
        .claimId("claim_123")
        .uniqueFileNumber("110425/123")
        .netProfitCosts(10.0)
        .netDisbursementAmount(50.0)
        .disbursementVatAmount(25.0)
        .vatIndicator(true)
        .netTravelCosts(5.0)
        .netWaitingCosts(5.0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("PROP1")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("POC_FS2022").build())
        .categoryType(PRE_ORDER_COVER)
        .feeType(HOURLY)
        .upperCostLimit(BigDecimal.valueOf(10000.00))
        .build();

    FeeCalculationResponse response = preOrderCoverHourlyRateCalculator.calculate(feeCalculationRequest, feeEntity);

    // calculatedVatAmount = 20% of (10+5+5) = 4.0
    // maxDisbVat = 20% of 50 = 10.0 (submitted 25 is capped to 10)
    // total = 20 (profitAndAdditional) + 4 (vat) + 50 (disbursement) + 10 (capped disbVat) = 84.0
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(84.0);
    assertThat(response.getFeeCalculation().getDisbursementVatAmount()).isEqualTo(10.0);
    assertThat(response.getFeeCalculation().getRequestedDisbursementVatAmount()).isEqualTo(25.0);
    assertThat(response.getValidationMessages()).hasSize(1);
    assertThat(response.getValidationMessages().get(0)).isEqualTo(
        ValidationMessagesInner.builder()
            .code(WARN_DISBURSEMENT_VAT_EXCEEDED.getCode())
            .message(WARN_DISBURSEMENT_VAT_EXCEEDED.getMessage())
            .type(WARNING)
            .build());
  }

  @Test
  void getSupportedCategories_shouldReturnPreOrderCover() {
    Set<CategoryType> result = preOrderCoverHourlyRateCalculator.getSupportedCategories();

    assertThat(result).containsExactly(PRE_ORDER_COVER);
  }
}

