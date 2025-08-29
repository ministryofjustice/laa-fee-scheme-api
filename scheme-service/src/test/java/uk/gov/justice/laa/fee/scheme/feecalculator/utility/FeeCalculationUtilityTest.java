package uk.gov.justice.laa.fee.scheme.feecalculator.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.justice.laa.fee.scheme.feecalculator.CalculationType.MENTAL_HEALTH;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.gov.justice.laa.fee.scheme.entity.FeeEntity;
import uk.gov.justice.laa.fee.scheme.entity.FeeSchemesEntity;
import uk.gov.justice.laa.fee.scheme.model.BoltOnType;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculation;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationRequest;
import uk.gov.justice.laa.fee.scheme.model.FeeCalculationResponse;

class FeeCalculationUtilityTest {

  @CsvSource(value = {
      "false, null, null, 94.96", // false VAT indicator
      "true, null, null, 106.88", // true VAT indicator
      "false, 2, 22.15, 139.26", // false VAT indicator with bolt ons
      "true, 2, 22.15, 160.04", // true VAT indicator with bolt ons
  }, nullValues = {"null"})
  @ParameterizedTest
  void calculate_givenFeeEntity_returnsFeeCalculationResponse(Boolean vatIndicator, Integer noBoltOns, BigDecimal boltOnFee,
                                                              double expectedTotal) {
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
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .fixedFee(new BigDecimal("59.62"))
        .adjornHearingBoltOn(boltOnFee)
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.calculate(feeEntity, feeCalculationRequest);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(expectedTotal);
  }

  @Test
  void calculate_givenFixedFee_returnsFeeCalculationResponse() {
    BigDecimal fixedFee = new BigDecimal("59.62");
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("FEE1")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("FEE_SCHEME_CODE").build())
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.calculate(fixedFee, feeCalculationRequest, feeEntity);

    assertThat(response).isNotNull();
    assertThat(response.getFeeCode()).isEqualTo("FEE1");

    FeeCalculation calculation = response.getFeeCalculation();
    assertThat(calculation).isNotNull();
    assertThat(calculation.getTotalAmount()).isEqualTo(106.88);
  }

  @Test
  void calculate_givenFixedFeeWithBoltOns_returnsFeeCalculationResponse() {
    BigDecimal fixedFee = new BigDecimal("263.00");
    FeeCalculationRequest feeCalculationRequest = FeeCalculationRequest.builder()
        .feeCode("MHL02")
        .startDate(LocalDate.of(2025, 1, 1))
        .vatIndicator(true)
        .netDisbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .boltOns(BoltOnType.builder()
            .boltOnAdjournedHearing(1)
            .build())
        .build();

    FeeEntity feeEntity = FeeEntity.builder()
        .feeCode("MHL02")
        .feeSchemeCode(FeeSchemesEntity.builder().schemeCode("MHL_FS2013").build())
        .fixedFee(fixedFee)
        .adjornHearingBoltOn(new BigDecimal(100))
        .calculationType(MENTAL_HEALTH)
        .build();

    FeeCalculationResponse response = FeeCalculationUtility.calculate(feeEntity, feeCalculationRequest);

    FeeCalculation expectedCalculation = FeeCalculation.builder()
        .totalAmount(470.94)
        .vatIndicator(true)
        .vatRateApplied(20.0)
        .disbursementAmount(29.45)
        .disbursementVatAmount(5.89)
        .fixedFeeAmount(263.00)
        .calculatedVatAmount(72.60)
        .boltOnFeeAmount(100.00)
        .build();

    FeeCalculationResponse expectedResponse = FeeCalculationResponse.builder()
        .feeCode("MHL02")
        .schemeId("MHL_FS2013")
        .claimId("temp hardcoded till clarification")
        .warnings(new ArrayList<>())
        .escapeCaseFlag(false)
        .feeCalculation(expectedCalculation)
        .build();

    assertThat(response)
        .usingRecursiveComparison()
        .isEqualTo(expectedResponse);

  }
}