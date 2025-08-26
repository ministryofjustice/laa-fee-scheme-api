package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeCalculationUtilityTest {

  @CsvSource(value = {
      "null, null, null, 89.07, 94.96",  // null VAT indicator
      "false, null, null, 89.07, 94.96", // false VAT indicator
      "true, null, null, 89.07, 106.88", // true VAT indicator
      "false, 2, 22.15, 133.37, 139.26", // false VAT indicator with bolt ons
      "true, 2, 22.15, 133.37, 160.04", // true VAT indicator with bolt ons
  }, nullValues = {"null"})
  @ParameterizedTest
  void shouldBuildFixedResponse(Boolean vatIndicator, Integer noBoltOns,  BigDecimal boltOnFee, double expectedSubTotal, double expectedTotal) {
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(vatIndicator)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .boltOns(noBoltOns != null ? BoltOnType.builder().boltOnAdjournedHearing(noBoltOns).build() : null)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("FEE1")
        .fixedFee(new BigDecimal("59.62"))
        .adjornHearingBoltOn(boltOnFee)
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.buildFixedFeeResponse(feeEntity, feeCalculationRequest);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getSubTotal()).isEqualTo(expectedSubTotal);
    assertThat(calculation.getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void shouldBuildFixedResponseForGivenFixedFee() {
    BigDecimal fixedFee = new BigDecimal("59.62");
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.buildFixedFeeResponse(fixedFee, feeCalculationRequest);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getSubTotal()).isEqualTo(89.07);
    assertThat(calculation.getTotalAmount()).isEqualTo(106.88);
  }

}