package uk.gov.justice.laa.fee.scheme.feecalculators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.gov.justice.laa.fee.scheme.feecalculators.CalculationType.MEDIATION;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class CalculateMediationFeeTest {

  @ParameterizedTest()
  @MethodSource("testData")
  void getFee_whenMediation_withMediationSessions(
      String description,
      String feeCode,
      boolean vatIndicator,
      Integer numberOfMediationSessions,
      double expectedSubTotal,
      double expectedTotal) {

    FeeCalculationRequest feeData = new FeeCalculationRequest();
    feeData.setFeeCode(feeCode);
    feeData.setStartDate(LocalDate.of(2025, 7, 29));
    feeData.setNetDisbursementAmount(50.50);
    feeData.setDisbursementVatAmount(20.15);
    feeData.setVatIndicator(vatIndicator);
    feeData.setNumberOfMediationSessions(numberOfMediationSessions);

    FeeEntity feeEntity = new FeeEntity();
    feeEntity.setFeeCode(feeCode);
    feeEntity.setTotalFee(new BigDecimal("75.50"));
    feeEntity.setMediationSessionOne(new BigDecimal("50"));
    feeEntity.setMediationSessionTwo(new BigDecimal("100"));
    feeEntity.setCalculationType(MEDIATION);

    FeeCalculationResponse response = CalculateMediationFee.getFee(feeEntity, feeData);

    assertNotNull(response.getFeeCalculation());
    assertThat(response.getFeeCode()).isEqualTo(feeCode);
    assertThat(response.getFeeCalculation().getSubTotal()).isCloseTo(expectedSubTotal, within(0.001));
    assertThat(response.getFeeCalculation().getTotalAmount()).isCloseTo(expectedTotal, within(0.001));
  }

  public static Stream<Arguments> testData() {
    return Stream.of(
        arguments("1 mediation session, VAT applied",  "MED1", true,  1,    100.50, 130.65),
        arguments("1 mediation session, no VAT",       "MED1", false, 1,    100.50, 120.65),
        arguments("2 mediation sessions, VAT applied", "MED1", true,  3,    150.50, 190.65),
        arguments("2 mediation sessions, no VAT",      "MED1", false, 3,    150.50, 170.65),
        arguments("More than 1 mediation session, VAT applied", "MED1", true,  3,    150.50, 190.65),
        arguments("More than 1 mediation session, no VAT",      "MED1", false, 3,    150.50, 170.65),
        arguments("No mediation sessions, VAT applied", "MAM1", true, null, 126.00, 161.25),
        arguments("No mediation sessions, no VAT",     "MAM1", false, null, 126.00, 146.15)
    );
  }

  private static Arguments arguments(String scenario, String feeCode, boolean vat, Integer sessions,
                                double subtotal, double total) {
    return Arguments.of(scenario, feeCode, vat, sessions, subtotal, total);
  }
}
