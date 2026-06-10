package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MENTAL_HEALTH;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
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
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.BoltOnFeeDetails;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class MentalHealthFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  MentalHealthFixedFeeCalculator mentalhealthFixedFeeCalculator;

  private static FeeCalculationResponse buildExpectedResponse(String feeCode, FeeCalculation expectedCalculation,
                                                              boolean hasEscaped,
                                                              List<ValidationMessagesInner> validationMessages) {
    return FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("MHL_FS2013")
        .claimId("claim_123")
        .validationMessages(validationMessages)
        .escapeCaseFlag(hasEscaped)
        .feeCalculation(expectedCalculation)
        .build();
  }

  private static FeeCalculation buildFeeCalculation(double fixedFee, boolean vatIndicator, Double calculatedVat,
                                                    Integer boltOnNumber, Double boltOnTotalFeeAmount,
                                                    Double boltOnAdjournedHearingFee, double expectedTotal) {
    return FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .disbursementAmount(50.50)
        .requestedNetDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .fixedFeeAmount(fixedFee)
        .calculatedVatAmount(calculatedVat)
        .boltOnFeeDetails(BoltOnFeeDetails.builder()
            .boltOnTotalFeeAmount(boltOnTotalFeeAmount)
            .boltOnAdjournedHearingFee(boltOnAdjournedHearingFee)
            .boltOnAdjournedHearingCount(boltOnNumber)
            .build())
        .build();
  }

  private static FeeEntity buildFeeEntity(String feeCode, double fixedFee, Double escapeThresholdLimit) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .fixedFee(BigDecimal.valueOf(fixedFee))
        .categoryType(MENTAL_HEALTH)
        .adjornHearingBoltOn(BigDecimal.valueOf(100.0))
        .escapeThresholdLimit(nonNull(escapeThresholdLimit) ? BigDecimal.valueOf(escapeThresholdLimit) : null)
        .build();
  }

  private static FeeCalculationRequest buildFeeCalculationRequest(String feeCode, boolean vatIndicator,
                                                                  Integer boltOnNumber, Double requestedNetProfitCosts) {
    return FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(vatIndicator)
        .boltOns(BoltOnType.builder().boltOnAdjournedHearing(boltOnNumber).build())
        .netProfitCosts(requestedNetProfitCosts)
        .build();
  }

  @Nested
  class MentalHealthFeeCalculationTest {

    public static Stream<Arguments> testData() {
      return Stream.of(
          arguments("MHL01, with Vat, no bolt ons", "MHL01", 253.0, true, 50.6, null,
              0.0, null, 374.25),

          arguments("MHL01, without vat, no bolt ons", "MHL01", 253.0, false, 0.0, null,
              0.0, null, 323.65),

          arguments("MHL05, with Vat, has bolt ons", "MHL05", 263.0, true, 112.6, 3,
              300.0, 300.0, 746.25),

          arguments("MHL05, without vat, has bolt ons", "MHL05", 263.0, false, 0.0, 3,
              300.0, 300.0, 633.65)
      );
    }

    private static Arguments arguments(String scenario, String feeCode, double fixedFee, boolean vat, Double calculatedVat,
                                       Integer boltOnNumber, Double boltOnTotalFeeAmount, Double boltOnAdjournedHearingFee,
                                       double expectedTotal) {

      return Arguments.of(scenario, feeCode, fixedFee, vat, calculatedVat, boltOnNumber, boltOnTotalFeeAmount,
          boltOnAdjournedHearingFee, expectedTotal);
    }

    @ParameterizedTest
    @MethodSource("testData")
    void getFee_whenMentalHealth(
        String description,
        String feeCode,
        double fixedFee,
        boolean vatIndicator,
        Double calculatedVat,
        Integer boltOnNumber,
        Double boltOnTotalFeeAmount,
        Double boltOnAdjournedHearingFee,
        double expectedTotal
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeData = buildFeeCalculationRequest(feeCode, vatIndicator, boltOnNumber, null);
      FeeEntity feeEntity = buildFeeEntity(feeCode, fixedFee, null);

      FeeCalculationResponse response = mentalhealthFixedFeeCalculator.calculate(feeData, feeEntity);

      FeeCalculation expectedCalculation = buildFeeCalculation(fixedFee, vatIndicator, calculatedVat, boltOnNumber,
          boltOnTotalFeeAmount, boltOnAdjournedHearingFee, expectedTotal);
      FeeCalculationResponse expectedResponse = buildExpectedResponse(feeCode, expectedCalculation,
          false, new ArrayList<>());

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }
  }

  @Nested
  class MentalHealthEscapeCaseTest {

    public static Stream<Arguments> testDataEscapeCase() {
      return Stream.of(
          argumentsEscapeCase("MHL01, with Vat, no bolt ons, escaped", "MHL01", 263.0,
              1000.0, true, 52.6, null,
              0.0, null, 386.25, true, 759.0),

          argumentsEscapeCase("MHL05, with Vat, has bolt ons, escaped", "MHL05", 321.0,
              1020.0, true, 124.2, 3,
              300.0, 300.0, 815.85, true, 321.0),

          argumentsEscapeCase("MHL05, with Vat, has bolt ons, not escaped", "MHL05", 321.0,
              111.0, true, 124.2, 3,
              300.0, 300.0, 815.85, false, 321.0),

          argumentsEscapeCase("MHL10, with Vat, has bolt ons, cannot escape", "MHL05", 129.0,
              1010.0, true, 85.8, 3,
              300.0, 300.0, 585.45, false, null)
      );
    }

    private static Arguments argumentsEscapeCase(String scenario, String feeCode, double fixedFee, double requestedNetProfitCosts,
                                                 boolean vat, Double calculatedVat, Integer boltOnNumber, Double boltOnTotalFeeAmount,
                                                 Double boltOnAdjournedHearingFee, double expectedTotal, boolean hasWarning,
                                                 Double escapeThresholdLimit) {

      return Arguments.of(scenario, feeCode, fixedFee, requestedNetProfitCosts, vat, calculatedVat, boltOnNumber,
          boltOnTotalFeeAmount, boltOnAdjournedHearingFee, expectedTotal, hasWarning, escapeThresholdLimit);
    }

    @ParameterizedTest
    @MethodSource("testDataEscapeCase")
    void getFee_whenMentalHealth_AndEscapeCase(
        String description,
        String feeCode,
        double fixedFee,
        double requestedNetProfitCosts,
        boolean vatIndicator,
        Double calculatedVat,
        Integer boltOnNumber,
        Double boltOnTotalFeeAmount,
        Double boltOnAdjournedHearingFee,
        double expectedTotal,
        boolean hasWarning,
        Double escapeThresholdLimit
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeData = buildFeeCalculationRequest(feeCode, vatIndicator, boltOnNumber, requestedNetProfitCosts);
      FeeEntity feeEntity = buildFeeEntity(feeCode, fixedFee, escapeThresholdLimit);

      FeeCalculationResponse response = mentalhealthFixedFeeCalculator.calculate(feeData, feeEntity);

      List<ValidationMessagesInner> validationMessages = new ArrayList<>();
      boolean hasEscaped = false;
      if (hasWarning) {
        ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
            .message(WarningType.WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD.getMessage())
            .code(WarningType.WARN_MENTAL_HEALTH_ESCAPE_THRESHOLD.getCode())
            .type(WARNING)
            .build();
        validationMessages.add(validationMessage);
        hasEscaped = true;
      }

      FeeCalculation expectedCalculation = buildFeeCalculation(fixedFee, vatIndicator, calculatedVat, boltOnNumber,
          boltOnTotalFeeAmount, boltOnAdjournedHearingFee, expectedTotal);
      FeeCalculationResponse expectedResponse = buildExpectedResponse(feeCode, expectedCalculation, hasEscaped, validationMessages);

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }

  }

  @Test
  void getSupportedCategories_shouldReturnEmptySet() {

    Set<CategoryType> result = mentalhealthFixedFeeCalculator.getSupportedCategories();

    assertThat(result).isEmpty();
  }

}