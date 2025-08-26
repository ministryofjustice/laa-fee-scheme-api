package uk.gov.justice.laa.fee.scheme.feecalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MEDIATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.exception.InvalidMediationSessionException;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class MediationFeeCalculatorTest {

  @ParameterizedTest
  @MethodSource("testData")
  void getFee_whenMediation(
      String description,
      String feeCode,
      boolean vatIndicator,
      Integer numberOfMediationSessions,
      double expectedTotal,
      BigDecimal fixedFee
  ) {

    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode(feeCode)
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(vatIndicator)
        .numberOfMediationSessions(numberOfMediationSessions)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode(feeCode)
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .fixedFee(fixedFee)
        .mediationSessionOne(new BigDecimal("50"))
        .mediationSessionTwo(new BigDecimal("100"))
        .calculationType(MEDIATION)
        .build();

    FeeCalculationResponse response = MediationFeeCalculator.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getTotalAmount()).isEqualTo(expectedTotal);
  }

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("1 mediation session, VAT applied",  "MED1", true,  1, 130.65, null),
        arguments("1 mediation session, no VAT",       "MED1", false, 1, 120.65, null),
        arguments("2 mediation sessions, VAT applied", "MED1", true,  2, 190.65, null),
        arguments("2 mediation sessions, no VAT",      "MED1", false, 2, 170.65, null),
        arguments("More than 1 mediation session, VAT applied", "MED1", true,  3, 190.65, null),
        arguments("More than 1 mediation session, no VAT",      "MED1", false, 3, 170.65, null),
        arguments("No mediation sessions, VAT applied", "MAM1", true, null, 161.25, new BigDecimal("75.50")),
        arguments("No mediation sessions, no VAT",     "MAM1", false, null, 146.15, new BigDecimal("75.50"))
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, Integer sessions,
                                     double total, BigDecimal fixedFee) {
    return Arguments.of(scenario, feeCode, vat, sessions, total, fixedFee);
  }

  @Test
  void getFee_whenMediationSessionIsNull_thenThrowsException() {
    FeeCalculationRequest feeData = FeeCalculationRequest.builder()
        .feeCode("MED1")
        .startDate(LocalDate.of(2025, 7, 29))
        .netDisbursementAmount(50.50)
        .disbursementVatAmount(20.15)
        .vatIndicator(true)
        .numberOfMediationSessions(0)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MED1")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .fixedFee(null)
        .mediationSessionOne(new BigDecimal("50"))
        .mediationSessionTwo(new BigDecimal("100"))
        .calculationType(MEDIATION)
        .build();

    assertThrows(InvalidMediationSessionException.class, () -> MediationFeeCalculator.getFee(feeEntity, feeData));
  }
}
