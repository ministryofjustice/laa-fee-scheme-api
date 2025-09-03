package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MEDIATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exception.InvalidMediationSessionException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class MediationFeeCalculatorTest {

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("1 mediation session, VAT applied", "MDAS2B", true, 1, 130.65, null,
            50.5, 20.15, 50, 10),
        arguments("1 mediation session, no VAT", "MDAS2B", false, 1, 120.65, null,
            50.5, 20.15, 50, 0),
        arguments("2 mediation sessions, VAT applied", "MDAS2B", true, 2, 190.65, null,
            50.5, 20.15, 100, 20),
        arguments("2 mediation sessions, no VAT", "MDAS2B", false, 2, 170.65, null,
            50.5, 20.15, 100, 0),
        arguments("More than 1 mediation session, VAT applied", "MDAS2B", true, 3, 190.65, null,
            50.5, 20.15, 100, 20),
        arguments("More than 1 mediation session, no VAT", "MDAS2B", false, 3, 170.65, null,
            50.5, 20.15, 100, 0),
        arguments("No mediation sessions, VAT applied", "ASSA", true, null, 161.25, new BigDecimal("75.50"),
            50.5, 20.15, 75.5, 15.1),
        arguments("No mediation sessions, no VAT", "ASSA", false, null, 146.15, new BigDecimal("75.50"),
            50.5, 20.15, 75.5, 0)
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

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(vatIndicator)
        .numberOfMediationSessions(numberOfMediationSessions)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
        .fixedFee(fixedFee)
        .mediationFeeLower(new BigDecimal("50"))
        .mediationFeeHigher(new BigDecimal("100"))
        .calculationType(MEDIATION)
        .build();

    FeeCalculationResponse response = MediationFeeCalculator.getFee(feeEntity, feeData);

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
        .schemeId("MED_FS2013")
        .claimId("claim_123")
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);
  }

  @Test
  void getFee_whenMediationSessionIsNull_thenThrowsException() {
    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("MDAS2B")
        .claimId("claim_123")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MDAS2B")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MED_FS2013").build())
        .fixedFee(null)
        .mediationFeeLower(new BigDecimal("50"))
        .mediationFeeHigher(new BigDecimal("100"))
        .calculationType(MEDIATION)
        .build();

    assertThrows(InvalidMediationSessionException.class, () -> MediationFeeCalculator.getFee(feeEntity, feeData));
  }
}
