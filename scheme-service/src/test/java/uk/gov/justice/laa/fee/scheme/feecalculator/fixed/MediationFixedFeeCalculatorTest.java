package uk.gov.justice.laa.fee.scheme.feecalculator.fixed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static uk.gov.justice.laa.fee.scheme.enums.CategoryType.MEDIATION;
import static uk.gov.justice.laa.fee.scheme.enums.ErrorType.ERR_MEDIATION_SESSIONS;
import static uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner.TypeEnum.WARNING;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import uk.gov.justice.laa.fee.scheme.enums.WarningType;
import uk.gov.justice.laa.fee.scheme.exception.ValidationException;
import uk.gov.justice.laa.fee.scheme.feecalculator.BaseFeeCalculatorTest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;
import uk.gov.justice.laa.fee.scheme.model.ValidationMessagesInner;

@ExtendWith(MockitoExtension.class)
class MediationFixedFeeCalculatorTest extends BaseFeeCalculatorTest {

  @InjectMocks
  MediationFixedFeeCalculator mediationFeeCalculator;

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("1 mediation session, VAT applied", "MDAS2B", true, 1, 120.6, null,
            50.5, 10.1, 50, 10),
        arguments("1 mediation session, no VAT", "MDAS2B", false, 1, 110.6, null,
            50.5, 10.1, 50, 0),
        arguments("2 mediation sessions, VAT applied", "MDAS2B", true, 2, 180.6, null,
            50.5, 10.1, 100, 20),
        arguments("2 mediation sessions, no VAT", "MDAS2B", false, 2, 160.6, null,
            50.5, 10.1, 100, 0),
        arguments("More than 1 mediation session, VAT applied", "MDAS2B", true, 3, 180.6, null,
            50.5, 10.1, 100, 20),
        arguments("More than 1 mediation session, no VAT", "MDAS2B", false, 3, 160.6, null,
            50.5, 10.1, 100, 0),
        arguments("No mediation sessions, VAT applied", "ASSA", true, null, 151.2, new BigDecimal("75.50"),
            50.5, 10.1, 75.5, 15.1),
        arguments("No mediation sessions, no VAT", "ASSA", false, null, 136.1, new BigDecimal("75.50"),
            50.5, 10.1, 75.5, 0)
    );
  }

  public static Stream<Arguments> disbursementTestData() {
    return Stream.of(
            arguments("1 mediation session, VAT applied", "MDAS2B", true, 1, 120.6, null,
                    50.5, 10.1, 50, 10),
            arguments("1 mediation session, no VAT", "MDAS2B", false, 1, 110.6, null,
                    50.5, 10.1, 50, 0),
            arguments("2 mediation sessions, VAT applied", "MDAS2B", true, 2, 180.6, null,
                    50.5, 10.1, 100, 20),
            arguments("2 mediation sessions, no VAT", "MDAS2B", false, 2, 160.6, null,
                    50.5, 10.1, 100, 0),
            arguments("More than 1 mediation session, VAT applied", "MDAS2B", true, 3, 180.6, null,
                    50.5, 10.1, 100, 20),
            arguments("More than 1 mediation session, no VAT", "MDAS2B", false, 3, 160.6, null,
                    50.5, 10.1, 100, 0),
            arguments("No mediation sessions, VAT applied", "ASSA", true, null, 151.2, new BigDecimal("75.50"),
                    50.5, 10.1, 75.5, 15.1),
            arguments("No mediation sessions, no VAT", "ASSA", false, null, 136.1, new BigDecimal("75.50"),
                    50.5, 10.1, 75.5, 0)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, Integer sessions,
                                     double total, BigDecimal fixedFee, double expectedDisbursementAmount,
                                     double disbursementVatAmount, double expectedFixedFee, double expectedCalculatedVat) {
    return Arguments.of(scenario, feeCode, vat, sessions, total, fixedFee, expectedDisbursementAmount, disbursementVatAmount,
        expectedFixedFee, expectedCalculatedVat);
  }

  @ParameterizedTest
  @MethodSource("testData")
  void getFee_whenMediation(
      String description,
      String feeCode,
      boolean vatIndicator,
      Integer numberOfMediationSessions,
      double expectedTotal,
      BigDecimal fixedFee,
      double expectedDisbursementAmount,
      double disbursementVatAmount,
      double expectedFixedFee,
      double expectedCalculatedVat
  ) {

    mockVatRatesService(vatIndicator);

    if (!vatIndicator) {
      mockVatRatesVatIndicatorTrue();
    }

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(10.10)
        .vatIndicator(vatIndicator)
        .caseConcludedDate(LocalDate.of(2025, 7, 29))
        .numberOfMediationSessions(numberOfMediationSessions)
        .build();


    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
        .fixedFee(fixedFee)
        .mediationFeeLower(new BigDecimal("50"))
        .mediationFeeHigher(new BigDecimal("100"))
        .categoryType(MEDIATION)
        .build();

    FeeCalculationResponse response = mediationFeeCalculator.calculate(feeData, feeEntity);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(expectedTotal)
        .vatIndicator(vatIndicator)
        .vatRateApplied(vatIndicator ? 20.0 : null)
        .disbursementAmount(expectedDisbursementAmount)
        .requestedNetDisbursementAmount(expectedDisbursementAmount)
        .disbursementVatAmount(disbursementVatAmount)
        .requestedDisbursementVatAmount(disbursementVatAmount)
        .fixedFeeAmount(expectedFixedFee)
        .calculatedVatAmount(expectedCalculatedVat)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode(feeCode)
        .schemeId("MED_FS2013")
        .claimId("claim_123")
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @ParameterizedTest
  @MethodSource("disbursementTestData")
  void getFee_whenMediation_andDisbursementVatOverLimit(
          String description,
          String feeCode,
          boolean vatIndicator,
          Integer numberOfMediationSessions,
          double expectedTotal,
          BigDecimal fixedFee,
          double expectedDisbursementAmount,
          double disbursementVatAmount,
          double expectedFixedFee,
          double expectedCalculatedVat
  ) {

    mockVatRatesService(vatIndicator);

    if (!vatIndicator) {
      mockVatRatesVatIndicatorTrue();
    }

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
            .feeCode(feeCode)
            .claimId("claim_123")
            .startDate(LocalDate.of(2025, 7, 29))
            .netDisbursementAmount(50.50)
            .disbursementVatAmount(13.26)
            .vatIndicator(vatIndicator)
            .caseConcludedDate(LocalDate.of(2025, 7, 29))
            .numberOfMediationSessions(numberOfMediationSessions)
            .build();


    FeeEntity feeEntity = FeeEntity.builder()
            .feeCode(feeCode)
            .feeScheme(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
            .fixedFee(fixedFee)
            .mediationFeeLower(new BigDecimal("50"))
            .mediationFeeHigher(new BigDecimal("100"))
            .categoryType(MEDIATION)
            .build();

    FeeCalculationResponse response = mediationFeeCalculator.calculate(feeData, feeEntity);

    ValidationMessagesInner validationMessage = ValidationMessagesInner.builder()
            .message(WarningType.WARN_DISBURSEMENT_VAT_LIMIT_REACHED.getMessage())
            .code(WarningType.WARN_DISBURSEMENT_VAT_LIMIT_REACHED.getCode())
            .type(WARNING)
            .build();

    FeeCalculation expectedCalculation = FeeCalculation.builder()
            .totalAmount(expectedTotal)
            .vatIndicator(vatIndicator)
            .vatRateApplied(vatIndicator ? 20.0 : null)
            .disbursementAmount(expectedDisbursementAmount)
            .requestedNetDisbursementAmount(expectedDisbursementAmount)
            .disbursementVatAmount(disbursementVatAmount)
            .requestedDisbursementVatAmount(feeData.getDisbursementVatAmount())
            .fixedFeeAmount(expectedFixedFee)
            .calculatedVatAmount(expectedCalculatedVat)
            .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
            .feeCode(feeCode)
            .schemeId("MED_FS2013")
            .claimId("claim_123")
            .feeCalculation(expectedCalculation)
            .validationMessages(List.of(validationMessage))
            .build();

    assertThat(response)
            .usingRecursiveComparison()
            .isEqualTo(expectedResponse);
  }

  @Test
  void getFee_whenMediationSessionIsNull_thenThrowsException() {

    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MDAS2B")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(0)
        .caseConcludedDate(LocalDate.of(2025, 7, 29))
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MDAS2B")
        .feeScheme(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
        .fixedFee(null)
        .mediationFeeLower(new BigDecimal("50"))
        .mediationFeeHigher(new BigDecimal("100"))
        .categoryType(MEDIATION)
        .build();

    assertThatThrownBy(() -> mediationFeeCalculator.calculate(feeCalculationRequest, feeEntity))
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("error", ERR_MEDIATION_SESSIONS)
        .hasMessage("ERRMED1 - Number of Mediation Sessions must be entered for the Fee Code used.");
  }

  @Test
  void getSupportedCategories_shouldReturnMediationCategory() {
    Set<CategoryType> result = mediationFeeCalculator.getSupportedCategories();

    assertThat(result).containsExactly(MEDIATION);
  }
}
