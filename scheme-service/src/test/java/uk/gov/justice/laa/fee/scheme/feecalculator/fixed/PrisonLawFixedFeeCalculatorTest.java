package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.PRISON_LAW;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_HAS_ESCAPED;
import static uk.gov.justice.laa.fee.scheme.enums.WarningType.WARN_PRISON_MAY_HAVE_ESCAPED;
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
import uk.gov.justice.laa.fee.scheme.enums.FeeType;
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class PrisonLawFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  private PrisonLawFixedFeeCalculator prisonLawFeeCalculator;

  @Test
  void testGetSupportedCategories() {
    Set<CategoryType> result = prisonLawFeeCalculator.getSupportedCategories();

    assertThat(result).containsExactly(PRISON_LAW);
  }

  private FeeEntity buildFeeEntity(String feeCode, double fixedFeeAmount, Double escapeThreshold, Double feeLimit) {
    return FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("PRISON_FS2016").build())
        .fixedFee(BigDecimal.valueOf(fixedFeeAmount))
        .categoryType(PRISON_LAW)
        .feeType(FeeType.FIXED)
        .escapeThresholdLimit(escapeThreshold != null ? BigDecimal.valueOf(escapeThreshold) : null)
        .totalLimit(feeLimit != null ? BigDecimal.valueOf(feeLimit) : null)
        .build();
  }

  private FeeCalculationRequest buildFeeCalculationRequest(
      String feeCode,
      String uniqueFileNumber,
      boolean vatIndicator,
      double disbursementAmount,
      double disbursementVatAmount,
      Double profitCosts,
      Double waitingCosts
  ) {
    return FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2017, 7, 29))
        .vatIndicator(vatIndicator)
        .uniqueFileNumber(uniqueFileNumber)
        .netDisbursementAmount(disbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .netProfitCosts(profitCosts)
        .netWaitingCosts(waitingCosts)
        .build();
  }

  @Nested
  class PrisonLawFeeCalculationTest {

    private static Stream<Arguments> testDataForPrisonLawClaims() {
      return Stream.of(
          arguments("PRIA Prison Law Fee Code, VAT applied", "PRIA",
              "121221/799", true, 200.75, 100.00,
              20.00, 360.9, 200.75, 40.15),

          arguments("PRIB1 Prison Law Fee Code, VAT not applied", "PRIB1",
              "121216/899", true, 203.93, 100.00,
              20.00, 364.72, 203.93, 40.79)
      );
    }

    private static Arguments arguments(String testDescription,
                                       String feeCode,
                                       String uniqueFileNumber,
                                       boolean vatIndicator,
                                       double fixedFeeAmount,
                                       double disbursementAmount,
                                       double disbursementVatAmount,
                                       double expectedTotal,
                                       double expectedFixedFee,
                                       double expectedCalculatedVat) {
      return Arguments.of(testDescription, feeCode, uniqueFileNumber, vatIndicator,
          fixedFeeAmount, disbursementAmount, disbursementVatAmount, expectedTotal, expectedFixedFee,
          expectedCalculatedVat);
    }

    // Positive scenario
    @ParameterizedTest
    @MethodSource("testDataForPrisonLawClaims")
    void test_whenClaimsSubmittedForPrisonLaw_shouldReturnFee(
        String description,
        String feeCode,
        String uniqueFileNumber,
        boolean vatIndicator,
        double fixedFeeAmount,
        double disbursementAmount,
        double disbursementVatAmount,
        double expectedTotal,
        double expectedFixedFee,
        double expectedCalculatedVat
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildFeeCalculationRequest(feeCode, uniqueFileNumber, vatIndicator,
          disbursementAmount, disbursementVatAmount, null, null
      );

      FeeEntity feeEntity = buildFeeEntity(feeCode, fixedFeeAmount, null, null);

      FeeCalculationResponse response = prisonLawFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .fixedFeeAmount(expectedFixedFee)
          .calculatedVatAmount(expectedCalculatedVat)
          .disbursementAmount(100.0)
          .disbursementVatAmount(20.0)
          .requestedNetDisbursementAmount(100.0)
          .build();

      FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
          .feeCode(feeCode)
          .schemeId("PRISON_FS2016")
          .validationMessages(new ArrayList<>())
          .escapeCaseFlag(false)
          .feeCalculation(expectedCalculation)
          .build();

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }

  }

  @Nested
  class PrisonLawEscapeCaseTest {

    private static Stream<Arguments> testDataForPrisonLawEscapeLogic() {
      return Stream.of(
          argumentsEscape("PRIC2,escape using escape case threshold, above limit", "PRIC2",
              "121221/799", true, 200.75, 100.00, 20.00,
              360.9, 200.75, 40.15, 1000.0,
              500.0, 1454.44, null, "WARCRM6", true),

          argumentsEscape("PRID1, escape using fee limit, above limit", "PRID1",
              "121216/899", true, 203.93, 100.00, 20.00,
              364.72, 203.93, 40.79, 250.0,
              120.0, null, 357.06, "WARCRM5", false),

          argumentsEscape("PRIC2,escape using escape case threshold, below limit", "PRIC2",
              "121221/799", true, 200.75, 100.00, 20.00,
              360.9, 200.75, 40.15, 1000.0, 200.0,
              1454.44, null, null, false),

          argumentsEscape("PRID1, escape using fee limit, below limit", "PRID1",
              "121216/899", true, 203.93, 100.00, 20.00,
              364.72, 203.93, 40.79, 250.0, 60.0,
              null, 357.06, null, false)
      );
    }

    private static Arguments argumentsEscape(String testDescription,
                                             String feeCode,
                                             String uniqueFileNumber,
                                             boolean vatIndicator,
                                             double fixedFeeAmount,
                                             double disbursementAmount,
                                             double disbursementVatAmount,
                                             double expectedTotal,
                                             double expectedFixedFee,
                                             double expectedCalculatedVat,
                                             Double requestedNetProfitCosts,
                                             Double requestedNetWaitingCosts,
                                             Double escapeThresholdLimit,
                                             Double feeLimit,
                                             String warningMessage,
                                             boolean hasEscaped) {
      return Arguments.of(testDescription, feeCode, uniqueFileNumber, vatIndicator,
          fixedFeeAmount, disbursementAmount, disbursementVatAmount, expectedTotal, expectedFixedFee,
          expectedCalculatedVat, requestedNetProfitCosts, requestedNetWaitingCosts, escapeThresholdLimit, feeLimit,
          warningMessage, hasEscaped);
    }

    @ParameterizedTest
    @MethodSource("testDataForPrisonLawEscapeLogic")
    void test_whenClaimsSubmittedForPrisonLaw_escapeLogic(
        String description,
        String feeCode,
        String uniqueFileNumber,
        boolean vatIndicator,
        double fixedFeeAmount,
        double disbursementAmount,
        double disbursementVatAmount,
        double expectedTotal,
        double expectedFixedFee,
        double expectedCalculatedVat,
        Double requestedNetProfitCosts,
        Double requestedNetWaitingCosts,
        Double escapeThresholdLimit,
        Double feeLimit,
        String warningMessage,
        boolean hasEscaped
    ) {

      mockVatRatesService(vatIndicator);

      FeeCalculationRequest feeCalculationRequest = buildFeeCalculationRequest(feeCode, uniqueFileNumber, vatIndicator,
          disbursementAmount, disbursementVatAmount, requestedNetProfitCosts, requestedNetWaitingCosts);

      FeeEntity feeEntity = buildFeeEntity(feeCode, fixedFeeAmount, escapeThresholdLimit, feeLimit);

      FeeCalculationResponse response = prisonLawFeeCalculator.calculate(feeCalculationRequest, feeEntity);

      List<ValidationMessagesInner> validationMessages = new ArrayList<>();
      if (nonNull(warningMessage)) {
        WarningType expectedWarning = "WARCRM5".equals(warningMessage)
            ? WARN_PRISON_MAY_HAVE_ESCAPED
            : WARN_PRISON_HAS_ESCAPED;

        ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
            .message(expectedWarning.getMessage())
            .code(expectedWarning.getCode())
            .type(WARNING)
            .build();
        validationMessages.add(validationMessage);
      }

      FeeCalculation expectedCalculation = FeeCalculation.builder()
          .totalAmount(expectedTotal)
          .vatIndicator(vatIndicator)
          .vatRateApplied(vatIndicator ? 20.0 : null)
          .fixedFeeAmount(expectedFixedFee)
          .calculatedVatAmount(expectedCalculatedVat)
          .disbursementAmount(100.0)
          .disbursementVatAmount(20.0)
          .requestedNetDisbursementAmount(100.0)
          .build();

      FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
          .feeCode(feeCode)
          .schemeId("PRISON_FS2016")
          .validationMessages(validationMessages)
          .escapeCaseFlag(hasEscaped)
          .feeCalculation(expectedCalculation)
          .build();

      assertThat(response)
          .usingRecursiveComparison()
          .isEqualTo(expectedResponse);
    }
  }
}
