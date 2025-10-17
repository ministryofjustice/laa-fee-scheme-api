package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.IMMIGRATION_ASYLUM;
import static uk.gov.justice.laa.fee.scheme.enums.FeeType.FIXED;
import static uk.gov.justice.laa.fee.scheme.feecalculator.fixed.ImmigrationAsylumFixedFeeCalculator.WARNING_MESSAGE_WARIA3;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.enums.CategoryType;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.EscapeCaseCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class ImmigrationAsylumFixedFeeCalculatorTest {

  @InjectMocks
  ImmigrationAsylumFixedFeeCalculator immigrationAsylumFixedFeeCalculator;

  @Nested
  class FeeCalculationTest {


    private FeeCalculationRequest buildRequest(String feeCode, double netDisbursementAmount, double disbursementVatAmount,
                                               boolean vatIndicator, String immigrationPriorAuthority,
                                               double detentionTravelAndWaitingCosts, double jrFormFilling) {
      return FeeCalculationRequest.builder()
          .feeCode(feeCode)
          .claimId("claim_123")
          .startDate(LocalDate.of(2025, 7, 29))
          .netDisbursementAmount(netDisbursementAmount)
          .disbursementVatAmount(disbursementVatAmount)
          .vatIndicator(vatIndicator)
          .immigrationPriorAuthorityNumber(immigrationPriorAuthority)
          .boltOns(BoltOnType.builder()
              .boltOnCmrhOral(2)
              .boltOnCmrhTelephone(2)
              .build())
          .detentionTravelAndWaitingCosts(detentionTravelAndWaitingCosts)
          .jrFormFilling(jrFormFilling)
          .build();
    }

    private FeeEntity buildFeeEntity(String feeCode, BigDecimal fixedFee, BigDecimal disbursementLimit, BigDecimal oralBoltOn,
                                     BigDecimal telephoneBoltOn) {
      return FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
          .fixedFee(fixedFee)
          .categoryType(IMMIGRATION_ASYLUM)
          .feeType(FIXED)
          .disbursementLimit(disbursementLimit)
          .oralCmrhBoltOn(oralBoltOn)
          .telephoneCmrhBoltOn(telephoneBoltOn)
          .build();
    }

    public static Stream<Arguments> testDataWithDisbursementWithinLimit() {
      return Stream.of(
          arguments("IACA, Has Vat, eligible for disbursement, below limit",
              "IACA", true, null, 399, 50, BigDecimal.valueOf(600),
              50, 50, 1274.0, 399, 512,
              137.5, 75.5),
          arguments("IACA, Has Vat, eligible for disbursement, above limit, with prior auth",
              "IACA", true, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
              50, 50, 1725.0, 800, 512,
              137.5, 75.5),
          arguments("IACA, No Vat, eligible for disbursement, below limit",
              "IACA", false, null, 399, 50, BigDecimal.valueOf(600),
              50, 50, 1136.50, 399, 512,
              0, 75.5),
          arguments("IACA, No Vat, eligible for disbursement, above limit, with prior auth",
              "IACA", false, "hasPriorAuth", 800, 100, BigDecimal.valueOf(600),
              50, 50, 1587.50, 800, 512,
              0, 75.5)
      );
    }

    private static Arguments arguments(String scenario, String feeCode, boolean vat, String priorAuthority, double netDisbursementAmount,
                                       double disbursementVatAmount, BigDecimal netDisbursementLimit, double detentionTravelAndWaitingCosts,
                                       double jrFormfilling, double total, double requestedDisbursementAmount,
                                       double expectedTotalBoltOnFeeAmount, double expectedCalculatedVatAmount,
                                       double expectedFixedFeeAmount) {
      return Arguments.of(scenario, feeCode, vat, priorAuthority, netDisbursementAmount, disbursementVatAmount, netDisbursementLimit,
          detentionTravelAndWaitingCosts, jrFormfilling, total, requestedDisbursementAmount, expectedTotalBoltOnFeeAmount,
          expectedCalculatedVatAmount, expectedFixedFeeAmount);
    }

    @ParameterizedTest
    @MethodSource("testDataWithDisbursementWithinLimit")
    void calculate_whenImmigrationAndAsylum_withDisbursement(
        String description,
        String feeCode,
        boolean vatIndicator,
        String immigrationPriorityAuthority,
        double netDisbursementAmount,
        double disbursementVatAmount,
        BigDecimal netDisbursementLimit,
        double detentionTravelAndWaitingCosts,
        double jrFormfilling,
        double expectedTotal,
        double requestedDisbursementAmount,
        double expectedTotalBoltOnFeeAmount,
        double expectedCalculatedVat,
        double expectedFixedFee) {

      FeeCalculationRequest feeData = buildRequest(feeCode, netDisbursementAmount, disbursementVatAmount, vatIndicator,
          immigrationPriorityAuthority, detentionTravelAndWaitingCosts, jrFormfilling);

      FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(expectedFixedFee), netDisbursementLimit,
          BigDecimal.valueOf(166), BigDecimal.valueOf(90));

      FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeData, feeEntity);

      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .disbursementAmount(requestedDisbursementAmount)
          .requestedNetDisbursementAmount(feeData.getNetDisbursementAmount())
          .disbursementVatAmount(disbursementVatAmount)
          .detentionTravelAndWaitingCostsAmount(detentionTravelAndWaitingCosts)
          .jrFormFillingAmount(jrFormfilling)
          .boltOnFeeDetails(BoltOnFeeDetails.builder()
              .boltOnTotalFeeAmount(expectedTotalBoltOnFeeAmount)
              .boltOnCmrhOralCount(2)
              .boltOnCmrhOralFee(332.0)
              .boltOnCmrhTelephoneCount(2)
              .boltOnCmrhTelephoneFee(180.0)
              .build())
          .fixedFeeAmount(expectedFixedFee)
          .calculatedVatAmount(expectedCalculatedVat)
          .build();

      FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
          .feeCode(feeCode)
          .schemeId("IMM_ASYLM_FS2023")
          .claimId("claim_123")
          .validationMessages(new ArrayList<>())
          .escapeCaseFlag(false)
          .feeCalculation(expectedCalculation)
          .build();

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }


    @ParameterizedTest
    @CsvSource({
        "IDAS1, 1110.6, 1000.0",
        "IDAS2, 1110.6, 1000.0"
    })
    void calculate_whenImmigrationAndAsylum_withNoDisbursementLimit(String feeCode, double expectedTotal,
                                                                    double requestedDisbursementAmount) {

      FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, requestedDisbursementAmount, 20.0,
          true, null, 0.0, 0.0
      );

      FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), null, null, null);

      FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      assertNotNull(response.getFeeCalculation());
      assertThat(response.getValidationMessages()).isEqualTo(new ArrayList<>());
      assertThat(response.getFeeCode()).isEqualTo(feeCode);
      assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    }

    @ParameterizedTest
    @CsvSource({
        "IALB, 510.6, 1000.0, 400.0, WARIA_2",
        "IMLB, 510.6, 1000.0, 400.0, WARIA_2",
        "IACE, 710.6, 1000.0, 600.0, WARIA_1"
    })
    void calculate_whenImmigrationAndAsylum_withDisbursementBeyondLimit(String feeCode, double expectedTotal,
                                                                        double requestedDisbursementAmount,
                                                                        double disbursementLimit, String warningMessage) {

      FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, requestedDisbursementAmount, 20.0,
          true, null, 0.0, 0.0);

      FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), BigDecimal.valueOf(disbursementLimit),
          null, null);

      FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      String expectedMessage = "WARIA_1".equals(warningMessage)
          ? ImmigrationAsylumFixedFeeCalculator.WARNING_MESSAGE_WARIA1
          : ImmigrationAsylumFixedFeeCalculator.WARNING_MESSAGE_WARIA2;

      ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
          .message(expectedMessage)
          .type(WARNING)
          .build();

      assertNotNull(response.getFeeCalculation());
      assertThat(response.getValidationMessages()).containsExactly(validationMessage);
      assertThat(response.getFeeCode()).isEqualTo(feeCode);
      assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
    }

  }

  @Nested
  class EscapeCaseTest {

    private FeeCalculationRequest buildRequest(String feeCode, Double netDisbursementAmount, Double requestedNetProfitCosts,
                                               Double requestedNetCounselCosts) {
      return FeeCalculationRequest.builder()
          .feeCode(feeCode)
          .claimId("claim_123")
          .startDate(LocalDate.of(2025, 7, 29))
          .netDisbursementAmount(netDisbursementAmount)
          .disbursementVatAmount(20.0)
          .vatIndicator(true)
          .immigrationPriorAuthorityNumber(null)
          .netProfitCosts(requestedNetProfitCosts)
          .netCostOfCounsel(requestedNetCounselCosts)
          .boltOns(BoltOnType.builder()
              .boltOnCmrhOral(4)
              .build())
          .build();
    }

    private FeeEntity buildFeeEntity(String feeCode, BigDecimal fixedFee, BigDecimal disbursementLimit,
                                     Double substantiveBoltOnCost, BigDecimal escapeThresholdLimit) {
      return FeeEntity.builder()
          .feeCode(feeCode)
          .feeScheme(FeeSchemesEntity.builder().schemeCode("IMM_ASYLM_FS2023").build())
          .fixedFee(fixedFee)
          .categoryType(IMMIGRATION_ASYLUM)
          .feeType(FIXED)
          .disbursementLimit(disbursementLimit)
          .oralCmrhBoltOn(BigDecimal.valueOf(166))
          .substantiveHearingBoltOn(nonNull(substantiveBoltOnCost) ? BigDecimal.valueOf(substantiveBoltOnCost) : null)
          .escapeThresholdLimit(escapeThresholdLimit)
          .build();
    }

    private void assertEscapeCaseCalculation(FeeCalculationResponse response, double calculatedEscapeCaseValue,
                                             double requestedNetProfitCosts, Double requestedNetCounselCosts,
                                             double escapeThresholdLimit) {
      EscapeCaseCalculation escapeCaseCalculation = response.getEscapeCaseCalculation();
      assertThat(escapeCaseCalculation).isNotNull();
      assertThat(escapeCaseCalculation.getCalculatedEscapeCaseValue()).isEqualTo(calculatedEscapeCaseValue);
      assertThat(escapeCaseCalculation.getEscapeCaseThreshold()).isEqualTo(escapeThresholdLimit);
      assertThat(escapeCaseCalculation.getNetProfitCostsAmount()).isEqualTo(requestedNetProfitCosts);
      assertThat(escapeCaseCalculation.getRequestedNetProfitCostsAmount()).isEqualTo(requestedNetProfitCosts);
      if (requestedNetCounselCosts == null) {
        assertThat(escapeCaseCalculation.getNetCostOfCounselAmount()).isNull();
      } else {
        assertThat(escapeCaseCalculation.getNetCostOfCounselAmount()).isEqualTo(requestedNetCounselCosts);
      }
    }

    @ParameterizedTest
    @CsvSource({
        "IDAS1, IDAS2"
    })
    void shouldNotCalculate_escapeCase_whenCaseCannotEscape(String feeCode) {
      FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, null, null, null);
      FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), null, null, null);
      FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      assertThat(response.getEscapeCaseFlag()).isFalse();
    }

    public static Stream<Arguments> escapeCaseData() {
      return Stream.of(
          arguments("IACE, past escape threshold, counsel cost eligible, no substantive bolt on", "IACE",
              1000.0, 1199.0, null, 1338.0,
              true, 1535.0),
          arguments("IACE, not past escape threshold, counsel cost eligible, no substantive bolt on", "IACE",
              1000.0, 199.0, null, 1338.0,
              false, null),
          arguments("IMLB, past escape threshold, counsel cost not eligible, no substantive bolt on", "IMLB",
              1500.0, null, null, 468.0,
              true, 836.0),
          arguments("IMLB, not past escape threshold, counsel cost not eligible, no substantive bolt on", "IMLB",
              100.0, null, null, 468.0,
              false, null),
          arguments("IMCF, past escape threshold, has substantive bolt on", "IMCF",
              1500.0, 1000.0, 237.0, 1710.0,
              true, 1836.0),
          arguments("IMCF, not past escape threshold, has substantive bolt on", "IMCF",
              1000.0, 500.0, 237.0, 1710.0,
              false, null)
      );
    }

    private static Arguments arguments(String scenario, String feeCode, double requestedNetProfitCosts, Double requestedNetCounselCosts,
                                       Double substantiveBoltOnCost, double escapeThresholdLimit, boolean hasWarning,
                                       Double calculatedEscapeCaseValue) {

      return Arguments.of(scenario, feeCode, requestedNetProfitCosts, requestedNetCounselCosts, substantiveBoltOnCost,
          escapeThresholdLimit, hasWarning, calculatedEscapeCaseValue);
    }

    @ParameterizedTest
    @MethodSource("escapeCaseData")
    void calculate_isEscapeCase(
        String description,
        String feeCode,
        double requestedNetProfitCosts,
        Double requestedNetCounselCosts,
        Double substantiveBoltOnCost,
        double escapeThresholdLimit,
        boolean hasWarning,
        Double calculatedEscapeCaseValue)
    {

      FeeCalculationRequest feeCalculationRequest = buildRequest(feeCode, null,
          requestedNetProfitCosts, requestedNetCounselCosts);

      FeeEntity feeEntity = buildFeeEntity(feeCode, BigDecimal.valueOf(75.5), BigDecimal.valueOf(600),
          substantiveBoltOnCost, BigDecimal.valueOf(escapeThresholdLimit));

      FeeCalculationResponse response = immigrationAsylumFixedFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      if (hasWarning) {
        ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
            .message(WARNING_MESSAGE_WARIA3)
            .type(WARNING)
            .build();
        assertThat(response.getValidationMessages()).containsExactly(validationMessage);
        assertThat(response.getEscapeCaseFlag()).isTrue();
        assertEscapeCaseCalculation(response, calculatedEscapeCaseValue, requestedNetProfitCosts, requestedNetCounselCosts, escapeThresholdLimit);
      } else  {
        assertThat(response.getValidationMessages()).isEmpty();
        assertThat(response.getEscapeCaseFlag()).isFalse();
      }
    }
  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {
    Set<CategoryType> result = immigrationAsylumFixedFeeCalculator.getSupportedCategories();
    assertThat(result).isEmpty();
  }

}
